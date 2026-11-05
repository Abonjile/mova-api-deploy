/**
 * =====================================================================================
 * ROUTE: analyticsRoutes.js
 * =====================================================================================
 * GET /api/analytics/:userId?period=Weekly|Monthly  Totals from completed exercises.
 * PUT /api/analytics/:userId?period=Weekly|Monthly  Saves the goal for that period.
 *
 * Totals come from ExerciseLog, not WorkoutLog. A user who quits a workout still gets
 * credit for every exercise they finished.
 * =====================================================================================
 */

const express = require('express');
const router = express.Router();
const Analytics = require('../models/Analytics');
const WorkoutLog = require('../models/WorkoutLog');
const ExerciseLog = require('../models/ExerciseLog');
const Exercise = require('../models/Exercise');
const { normalisePeriod, getPeriodRange, bucketIndex } = require('../utils/period');

function muscleBucket(group = '') {
  const g = group.toLowerCase();
  if (g.includes('core') || g.includes('abs')) return 'core';
  if (g.includes('leg') || g.includes('lower') || g.includes('glute')) return 'lower';
  return 'upper';
}

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

    // 1. This user's sessions inside the window. ExerciseLog has no userId, so each
    //    exercise is linked back to its session through workoutLogId.
    const sessions = await WorkoutLog.find({
      userId,
      createdAt: { $gte: range.start, $lte: range.end },
    }).select('workoutLogId createdAt').lean();

    const sessionDate = new Map(sessions.map(s => [s.workoutLogId, s.createdAt]));
    const sessionIds = [...sessionDate.keys()];

    // 2. Completed exercises only. Exercises finished before a quit still count.
    const exerciseLogs = sessionIds.length === 0
      ? []
      : await ExerciseLog.find({ workoutLogId: { $in: sessionIds }, completed: true }).lean();

    // 3. One query for the muscle group of every exercise used.
    const exerciseIds = [...new Set(exerciseLogs.map(e => e.exerciseId))];
    const exercises = exerciseIds.length === 0
      ? []
      : await Exercise.find({ exerciseId: { $in: exerciseIds } }).lean();
    const groupById = new Map(exercises.map(e => [e.exerciseId, e.targetMuscleGroup]));

    const calorieTrend = Array(range.buckets).fill(0);
    const trainingSeconds = Array(range.buckets).fill(0);
    const counts = { core: 0, upper: 0, lower: 0 };
    let burnedCalories = 0;

    for (const ex of exerciseLogs) {
      const i = bucketIndex(sessionDate.get(ex.workoutLogId), range);
      const kcal = ex.caloriesBurned || 0;

      calorieTrend[i] += kcal;
      trainingSeconds[i] += ex.durationSeconds || 0;
      burnedCalories += kcal;

      const group = groupById.get(ex.exerciseId);
      if (group) counts[muscleBucket(group)] += ex.sets || 3;
    }

    const goal = await Analytics.findOneAndUpdate(
      { userId, period },
      {
        $setOnInsert: {
          goalTitle: `Burn 2000 cal this ${period === 'Monthly' ? 'month' : 'week'}`,
          targetCalories: 2000,
        },
      },
      { returnDocument: 'after', upsert: true, setDefaultsOnInsert: true }
    );

    res.status(200).json({
      userId,
      period,
      goalTitle: goal.goalTitle,
      targetCalories: goal.targetCalories,
      burnedCalories: Math.round(burnedCalories),
      calorieTrend: calorieTrend.map(k => Math.round(k)),
      trainingVolume: trainingSeconds.map(s => Math.round(s / 60)),
      muscleGroupDistribution: toPercentages(counts),
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// PUT /api/analytics/:userId?period=Weekly|Monthly
// body: { goalTitle, targetCalories }. Both are optional, but at least one is required.
router.put('/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    const period = normalisePeriod(req.query.period);
    const update = {};

    if (req.body.goalTitle !== undefined) {
      const title = String(req.body.goalTitle).trim();
      if (!title || title.length > 80) {
        return res.status(400).json({ error: 'goalTitle must be 1 to 80 characters' });
      }
      update.goalTitle = title;
    }

    if (req.body.targetCalories !== undefined) {
      const kcal = Number(req.body.targetCalories);
      if (!Number.isInteger(kcal) || kcal < 100 || kcal > 10000) {
        return res.status(400).json({ error: 'targetCalories must be a whole number from 100 to 10000' });
      }
      update.targetCalories = kcal;
    }

    if (Object.keys(update).length === 0) {
      return res.status(400).json({ error: 'Nothing to update' });
    }

    update.updatedAt = new Date();

    const goal = await Analytics.findOneAndUpdate(
      { userId, period },
      { $set: update },
      { returnDocument: 'after', upsert: true, setDefaultsOnInsert: true }
    );

    res.status(200).json({ message: 'Goal updated successfully', goal });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;