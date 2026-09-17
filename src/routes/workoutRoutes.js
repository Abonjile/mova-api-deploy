const express = require('express');
const router = express.Router();
const Workout = require('../models/Workout');

// GET /api/workouts — Get all workouts for a user
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

// POST /api/workouts — Create a custom workout
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
    res.status(201).json({ message: 'Workout created successfully', workout });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// GET /api/workouts/:id — Get workout details
router.get('/:id', async (req, res) => {
  try {
    const workout = await Workout.findOne({ workoutId: req.params.id });
    if (!workout) {
      return res.status(404).json({ message: 'Workout not found' });
    }
    res.json(workout);
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
    workout.updatedAt = new Date();
    
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

// POST /api/workouts/log — Log a completed workout
router.post('/log', async (req, res) => {
  try {
    const { userId, workoutId, durationMinutes, caloriesBurned, completed } = req.body;
    
    // Find the workout
    const workout = await Workout.findOne({ workoutId });
    if (!workout) {
      return res.status(404).json({ message: 'Workout not found' });
    }
    
    // For now, just return success, to be updated later
    res.json({
      message: 'Workout logged successfully',
      log: {
        userId,
        workoutId,
        workoutName: workout.name,
        durationMinutes,
        caloriesBurned,
        completed,
        loggedAt: new Date()
      }
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;