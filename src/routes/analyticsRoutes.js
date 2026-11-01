/**
 * =====================================================================================
 * ROUTE: analyticsRoutes.js
 * =====================================================================================
 * GET  /api/analytics/:userId?period=  Derived analytics for the chosen window.
 * PUT  /api/analytics/:userId?period=  Updates the saved goal for that window.
 *
 * Every value except the goal is recalculated from workout logs on each GET. That
 * keeps the charts correct even when logs are edited or deleted.
 * =====================================================================================
 */

const express = require('express');
const router = express.Router();
const Analytics = require('../models/Analytics');
const WorkoutLog = require('../models/WorkoutLog');
const WorkoutExercise = require('../models/WorkoutExercise');
const Exercise = require('../models/Exercise');
const { normalisePeriod, getPeriodRange, bucketIndex } = require('../utils/period');

/**
 * Maps an exercise's target muscle group to one of the three groups the donut chart
 * shows. The check order matters: "core" is tested before "upper", so a group like
 * "upper core" counts as core.
 */
function muscleBucket(group = '') {
  const g = group.toLowerCase();
  if (g.includes('core') || g.includes('abs')) return 'core';
  if (g.includes('leg') || g.includes('lower') || g.includes('glute')) return 'lower';
  return 'upper';
}

/**
 * Converts raw set counts into whole-number percentages. The percentages are rounded,
 * so they may not add up to exactly 100. That is an acceptable display trade-off.
 */
function toPercentages(counts) {
  const total = counts.core + counts.upper + counts.lower;
  const pct = n => (total ? Math.round((n / total) * 100) : 0);
  return { core: pct(counts.core), upper: pct(counts.upper), lower: pct(counts.lower) };
}

// GET /api/analytics/:userId?period=Weekly|Monthly
router.get('/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    const period = normalisePeriod(req.query.period);
    const range = getPeriodRange(period);

    // Only completed logs inside the window count toward this period's totals.
    // Earlier versions read every log ever recorded, which is why the period
    // switcher appeared to do nothing.
    const logs = await WorkoutLog.find({
      userId,
      completed: true,
      createdAt: { $gte: range.start, $lte: range.end },
    }).lean(); // .lean() returns plain objects, which is faster for read-only work

    // Fill the chart buckets with zeros first, so days with no workouts show as empty
    // bars instead of being missing from the chart.
    const calorieTrend = Array(range.buckets).fill(0);
    const trainingVolume = Array(range.buckets).fill(0);
    let burnedCalories = 0;

    for (const log of logs) {
      const i = bucketIndex(log.createdAt, range);
      calorieTrend[i] += log.caloriesBurned || 0;
      trainingVolume[i] += log.durationMinutes || 0;
      burnedCalories += log.caloriesBurned || 0;
    }

    // Muscle distribution uses three batched queries in total. The earlier version
    // ran one query per log and one per exercise, which was slow as history grew.
    const workoutIds = [...new Set(logs.map(l => l.workoutId))];
    const workoutExercises = await WorkoutExercise.find({ workoutId: { $in: workoutIds } }).lean();
    const exerciseIds = [...new Set(workoutExercises.map(we => we.exerciseId))];
    const exercises = await Exercise.find({ exerciseId: { $in: exerciseIds } }).lean();

    // Lookup tables, so the counting loop below doesn't search arrays repeatedly.
    const groupById = new Map(exercises.map(e => [e.exerciseId, e.targetMuscleGroup]));
    const exercisesByWorkout = new Map();
    for (const we of workoutExercises) {
      if (!exercisesByWorkout.has(we.workoutId)) exercisesByWorkout.set(we.workoutId, []);
      exercisesByWorkout.get(we.workoutId).push(we);
    }

    // Each completed log counts its workout's exercises. A workout repeated across
    // several logs is counted once per log, which matches how much training was done.
    const counts = { core: 0, upper: 0, lower: 0 };
    for (const log of logs) {
      for (const we of exercisesByWorkout.get(log.workoutId) || []) {
        const group = groupById.get(we.exerciseId);
        if (group) counts[muscleBucket(group)] += we.sets || 3; // 3 sets is a sensible default
      }
    }

    // Returns the saved goal, creating the default the first time this period is read.
    // $setOnInsert only applies when a new document is created, so a saved goal is never
    // overwritten by a GET.
    const goal = await Analytics.findOneAndUpdate(
      { userId, period },
      {
        $setOnInsert: {
          goalTitle: `Burn 2000 cal this ${period === 'Monthly' ? 'month' : 'week'}`,
          targetCalories: 2000,
        },
      },
      { new: true, upsert: true, setDefaultsOnInsert: true }
    );

    res.status(200).json({
      userId,
      period,
      goalTitle: goal.goalTitle,
      targetCalories: goal.targetCalories,
      burnedCalories,
      calorieTrend,
      trainingVolume,
      muscleGroupDistribution: toPercentages(counts),
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// PUT /api/analytics/:userId?period=Weekly|Monthly
// body: { goalTitle, targetCalories }. Both fields are optional, but at least one is required.
router.put('/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    const period = normalisePeriod(req.query.period);
    const update = {};

    // Only fields that were actually sent are validated and updated. This allows
    // partial updates, such as changing just the title.
    if (req.body.goalTitle !== undefined) {
      const title = String(req.body.goalTitle).trim();
      if (!title || title.length > 80) {
        return res.status(400).json({ error: 'goalTitle must be 1 to 80 characters' });
      }
      update.goalTitle = title;
    }

    if (req.body.targetCalories !== undefined) {
      const kcal = Number(req.body.targetCalories);
      // The range check rejects values like 2000.5, "abc", or an accidental extra zero.
      if (!Number.isInteger(kcal) || kcal < 100 || kcal > 10000) {
        return res.status(400).json({ error: 'targetCalories must be a whole number from 100 to 10000' });
      }
      update.targetCalories = kcal;
    }

    if (Object.keys(update).length === 0) {
      return res.status(400).json({ error: 'Nothing to update' });
    }

    update.updatedAt = new Date();

    // upsert creates the goal if the user never had one for this period, so the PUT
    // works on a brand-new account.
    const goal = await Analytics.findOneAndUpdate(
      { userId, period },
      { $set: update },
      { new: true, upsert: true, setDefaultsOnInsert: true }
    );

    res.status(200).json({ message: 'Goal updated successfully', goal });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;