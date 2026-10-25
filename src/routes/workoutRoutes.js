const express = require('express');
const router = express.Router();
const Workout = require('../models/Workout');
const WorkoutExercise = require('../models/WorkoutExercise');
const WorkoutLog = require('../models/WorkoutLog');
const Exercise = require('../models/Exercise');
const ExerciseLog = require('../models/ExerciseLog');
const {
  calculateWorkoutCalories,
  calculateExerciseDuration,
  calculateExerciseCalories
} = require('../utils/calorieCalculator');

// GET /api/workouts?userId=xxx
router.get('/', async (req, res) => {
  try {
    const { userId, isCustom } = req.query;
    if (!userId) return res.status(400).json({ message: 'userId is required' });
    
    const filter = { userId };
    if (isCustom !== undefined) {
      filter.isCustom = isCustom === 'true';
    }
    
    const workouts = await Workout.find(filter);
    res.json(workouts);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/workouts — Create workout + junction entries
router.post('/', async (req, res) => {
  try {
    const { userId, name, description, difficulty, durationMinutes, exercises, iconName } = req.body;
    if (!userId || !name) {
      return res.status(400).json({ message: 'userId and name are required' });
    }

    const workout = new Workout({
      userId, name, description, difficulty, durationMinutes, isCustom: true, iconName: iconName || 'fitness'
    });
    await workout.save();

    if (exercises && exercises.length > 0) {
      const junctionEntries = exercises.map((ex, index) => ({
        workoutId: workout.workoutId,
        exerciseId: ex.exerciseId,
        orderNumber: index + 1,
        sets: ex.sets || 3,
        reps: ex.reps || 12,
        restSeconds: ex.restSeconds || 30
      }));
      await WorkoutExercise.insertMany(junctionEntries);
    }

    res.status(201).json(workout);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/workouts/start — Create a new workout session (empty WorkoutLog)
// Called when the user taps "Start Workout". Returns the workoutLogId so the
// client can attach per-exercise logs to this session as the user progresses.
router.post('/start', async (req, res) => {
  try {
    const { userId, workoutId } = req.body;

    if (!userId || !workoutId) {
      return res.status(400).json({ message: 'userId and workoutId are required' });
    }

    // Confirm the workout exists
    const workout = await Workout.findOne({ workoutId });
    if (!workout) {
      return res.status(404).json({ message: 'Workout not found' });
    }

    // Create an empty session log — totals are filled in when the workout finishes
    const workoutLog = new WorkoutLog({
      userId,
      workoutId,
      durationMinutes: 0,
      caloriesBurned: 0,
      weightAtTime: 0,
      completed: false,
      userNote: '',
      isSynced: true
    });

    await workoutLog.save();

    res.status(201).json({
      message: 'Workout session started',
      workoutLogId: workoutLog.workoutLogId,
      workoutId: workout.workoutId,
      userId: workoutLog.userId,
      startedAt: workoutLog.createdAt
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/workouts/log-exercise — Log one exercise within an active workout session
// Called each time a user finishes (or skips) an exercise.
// The client sends the workoutLogId created by /start.
router.post('/log-exercise', async (req, res) => {
  try {
    const {
      workoutLogId, workoutId, exerciseId, userId,
      orderNumber, sets, repsPerSet, difficulty,
      repsCompleted, completed, weightAtTime, genderAtTime
    } = req.body;

    if (!workoutLogId || !workoutId || !exerciseId || !userId) {
      return res.status(400).json({
        message: 'workoutLogId, workoutId, exerciseId, and userId are required'
      });
    }

    const workoutLog = await WorkoutLog.findOne({ workoutLogId });
    if (!workoutLog) {
      return res.status(404).json({ message: 'Workout session not found' });
    }

    const exercise = await Exercise.findOne({ exerciseId });
    if (!exercise) {
      return res.status(404).json({ message: 'Exercise not found' });
    }

    // Skipped exercises have zero duration and zero calories
    const isCompleted = completed !== false;
    const durationSeconds = isCompleted ? calculateExerciseDuration(sets || 3, difficulty || 'BEGINNER') : 0;
    const caloriesBurned = isCompleted
      ? calculateExerciseCalories(exercise.metValue || 3.8, weightAtTime || 70, durationSeconds, genderAtTime || 'MALE', true)
      : 0;

    const finalReps = isCompleted ? (repsCompleted || sets * repsPerSet || 0) : 0;

    const exerciseLog = new ExerciseLog({
      workoutLogId,
      exerciseId,
      orderNumber: orderNumber || 1,
      sets: sets || 3,
      repsPerSet: repsPerSet || 12,
      difficulty: difficulty || 'BEGINNER',
      durationSeconds,
      repsCompleted: finalReps,
      caloriesBurned,
      completed: isCompleted
    });

    await exerciseLog.save();

    res.status(201).json({
      message: isCompleted ? 'Exercise logged' : 'Exercise skipped',
      exerciseLog,
      caloriesBurned
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/workouts/finish — Finalise a workout session
// Called when the user completes the last exercise or taps Quit.
// Aggregates all ExerciseLog entries for the session into the WorkoutLog summary.
router.post('/finish', async (req, res) => {
  try {
    const { workoutLogId, userId, workoutId, completed, userNote } = req.body;

    if (!workoutLogId) {
      return res.status(400).json({ message: 'workoutLogId is required' });
    }

    // Confirm the session exists
    const workoutLog = await WorkoutLog.findOne({ workoutLogId });
    if (!workoutLog) {
      return res.status(404).json({ message: 'Workout session not found' });
    }

    // Fetch all exercise logs attached to this session
    const exerciseLogs = await ExerciseLog.find({ workoutLogId });

    // Aggregate totals
    const totalDurationSeconds = exerciseLogs.reduce((sum, el) => sum + (el.durationSeconds || 0), 0);
    const totalCaloriesBurned = exerciseLogs.reduce((sum, el) => sum + (el.caloriesBurned || 0), 0);
    const exercisesCompleted = exerciseLogs.filter(el => el.completed).length;
    const exercisesSkipped = exerciseLogs.filter(el => !el.completed).length;

    // Update the WorkoutLog with the summary
    workoutLog.durationMinutes = Math.round(totalDurationSeconds / 60);
    workoutLog.caloriesBurned = Math.round(totalCaloriesBurned);
    workoutLog.completed = completed !== false;
    workoutLog.userNote = userNote || '';
    workoutLog.updatedAt = new Date();

    await workoutLog.save();

    res.status(200).json({
      message: 'Workout session finished',
      workoutLogId: workoutLog.workoutLogId,
      totalDurationSeconds,
      totalDurationMinutes: Math.round(totalDurationSeconds / 60),
      totalCaloriesBurned: Math.round(totalCaloriesBurned),
      exercisesCompleted,
      exercisesSkipped,
      completed: workoutLog.completed
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/workouts/log — Log a completed workout
router.post('/log', async (req, res) => {
  try {
    const { userId, workoutId, durationMinutes, weightAtTime, userNote } = req.body;

    if (!userId || !workoutId || !durationMinutes || !weightAtTime) {
      return res.status(400).json({ message: 'userId, workoutId, durationMinutes, and weightAtTime are required' });
    }

    const junctionEntries = await WorkoutExercise.find({ workoutId });
    const exerciseIds = junctionEntries.map(e => e.exerciseId);
    const exercises = await Exercise.find({ exerciseId: { $in: exerciseIds } });

    const exercisesWithMet = junctionEntries.map(entry => {
      const ex = exercises.find(e => e.exerciseId === entry.exerciseId);
      return {
        metValue: ex ? ex.metValue : 3.8,
        durationMinutes: durationMinutes / junctionEntries.length
      };
    });

    const caloriesBurned = calculateWorkoutCalories(exercisesWithMet, weightAtTime);

    const log = new WorkoutLog({
      userId,
      workoutId,
      durationMinutes,
      caloriesBurned,
      weightAtTime,
      completed: true,
      userNote: userNote || '',
      isSynced: true
    });

    await log.save();
    res.status(201).json({
      message: 'Workout logged',
      log,
      calculatedCalories: caloriesBurned
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/workouts/:workoutId/exercises — Bulk add exercises to a workout
router.post('/:workoutId/exercises', async (req, res) => {
  try {
    const { workoutId } = req.params;
    const exercises = req.body;

    if (!Array.isArray(exercises) || exercises.length === 0) {
      return res.status(400).json({ message: 'Exercises array is required' });
    }

    const entries = exercises.map((ex, index) => ({
      workoutId: workoutId,
      exerciseId: ex.exerciseId,
      orderNumber: ex.orderNumber || index + 1,
      sets: ex.sets || 3,
      reps: ex.reps || 12,
      restSeconds: ex.restSeconds || 30
    }));

    const saved = await WorkoutExercise.insertMany(entries);
    res.status(201).json(saved);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// GET /api/workouts/:id — Get workout + its exercises
router.get('/:id', async (req, res) => {
  try {
    const workout = await Workout.findOne({ workoutId: req.params.id });
    if (!workout) return res.status(404).json({ message: 'Workout not found' });

    const exercises = await WorkoutExercise.find({ workoutId: workout.workoutId })
      .sort({ orderNumber: 1 });

    res.json({ ...workout.toObject(), exercises });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;