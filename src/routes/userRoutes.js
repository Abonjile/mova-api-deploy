const express = require('express');
const router = express.Router();
const User = require('../models/User');

// GET /api/users/:userId — Get user profile
router.get('/:userId', async (req, res) => {
  try {
    const user = await User.findOne({ userId: req.params.userId });
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }
    res.json({
      userId: user.userId,
      fullName: user.fullName,
      email: user.email,
      weight: user.weight,
      height: user.height,
      age: user.age,
      gender: user.gender,
      fitnessLevel: user.fitnessLevel,
      fitnessGoal: user.fitnessGoal
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// PUT /api/users/:userId — Update user profile
router.put('/:userId', async (req, res) => {
  try {
    const { weight, height, age, gender, fitnessLevel, fitnessGoal } = req.body;
    
    const user = await User.findOne({ userId: req.params.userId });
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }
    
    // Update fields
    if (weight) user.weight = weight;
    if (height) user.height = height;
    if (age) user.age = age;
    if (gender) user.gender = gender;
    if (fitnessLevel) user.fitnessLevel = fitnessLevel;
    if (fitnessGoal) user.fitnessGoal = fitnessGoal;
    user.updatedAt = new Date();
    
    await user.save();
    
    res.json({
      message: 'Profile updated successfully',
      user: {
        userId: user.userId,
        fullName: user.fullName,
        email: user.email,
        weight: user.weight,
        height: user.height,
        age: user.age,
        gender: user.gender,
        fitnessLevel: user.fitnessLevel,
        fitnessGoal: user.fitnessGoal
      }
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;