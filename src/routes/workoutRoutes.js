const express = require('express');
const router = express.Router();
const Workout = require('../models/Workout');
const WorkoutExercise = require('../models/WorkoutExercise');
const WorkoutLog = require('../models/WorkoutLog');
const Exercise = require('../models/Exercise');
const { calculateWorkoutCalories } = require('../utils/calorieCalculator');

// GET /api/workouts?userId=xxx — Get all workouts for a user
router.get('/', async (req, res) => {
  try {
    const { userId } = req.query;
    if (!userId) {
      return res.status(400).json({ message: 'userId is required' });
    }
    const workouts = await Workout.find({ userId });
    res.json(workouts);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/workouts — Create workout + junction entries
router.post('/', async (req, res) => {
  try {
    const { userId, name, description, difficulty, durationMinutes, exercises } = req.body;
    if (!userId || !name) {
      return res.status(400).json({ message: 'userId and name are required' });
    }

    const workout = new Workout({
      userId,
      name,
      description: description || '',
      difficulty: difficulty || 'BEGINNER',
      durationMinutes: durationMinutes || 20,
      exercises: exercises || [],
      isCustom: true
    });
    await workout.save();

    // Save junction entries if exercises were provided
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

    res.status(201).json({ message: 'Workout created successfully', workout });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/workouts/log — Log a completed workout
router.post('/log', async (req, res) => {
  try {
    const { userId, workoutId, durationMinutes, weightAtTime, userNote } = req.body;

    if (!userId || !workoutId || !durationMinutes || !weightAtTime) {
      return res.status(400).json({
        message: 'userId, workoutId, durationMinutes, and weightAtTime are required'
      });
    }

    // Get workout's exercises for MET values
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

// PUT /api/workouts/:id — Update workout
router.put('/:id', async (req, res) => {
  try {
    const workout = await Workout.findOne({ workoutId: req.params.id });
    if (!workout) {
      return res.status(404).json({ message: 'Workout not found' });
    }

    const { name, description, difficulty, durationMinutes, exercises } = req.body;
    if (name) workout.name = name;
    if (description) workout.description = description;
    if (difficulty) workout.difficulty = difficulty;
    if (durationMinutes) workout.durationMinutes = durationMinutes;
    if (exercises) workout.exercises = exercises;
    workout.updatedAt = new Date();   // ← correct field

    await workout.save();
    res.json({ message: 'Workout updated successfully', workout });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// DELETE /api/workouts/:id — Delete workout
router.delete('/:id', async (req, res) => {
  try {
    const workout = await Workout.findOneAndDelete({ workoutId: req.params.id });
    if (!workout) {
      return res.status(404).json({ message: 'Workout not found' });
    }
    res.json({ message: 'Workout deleted successfully' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;