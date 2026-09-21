const express = require('express');
const router = express.Router();
const Exercise = require('../models/Exercise');

// GET /api/exercises — Get all exercises
router.get('/', async (req, res) => {
  try {
    const exercises = await Exercise.find({ isActive: true });
    res.json(exercises);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// GET /api/exercises/:id — Get one exercise
router.get('/:id', async (req, res) => {
  try {
    const exercise = await Exercise.findOne({ exerciseId: req.params.id });
    if (!exercise) return res.status(404).json({ message: 'Exercise not found' });
    res.json(exercise);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/exercises — Create exercise (admin/seed)
router.post('/', async (req, res) => {
  try {
    const exercise = new Exercise(req.body);
    await exercise.save();
    res.status(201).json(exercise);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;