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
    res.json(user);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// PUT /api/users/:userId — Update user profile
router.put('/:userId', async (req, res) => {
  try {
    const { fullname, weight, height, age, gender, fitnessLevel, fitnessGoal, location } = req.body;
    
    // Build update object with only provided fields
    const update = { updatedAt: new Date() };
    if (fullName !== undefined) update.fullName = fullName;
    if (height !== undefined) update.height = height;
    if (weight !== undefined) update.weight = weight;
    if (age !== undefined) update.age = age;
    if (gender !== undefined) update.gender = gender;
    if (fitnessLevel !== undefined) update.fitnessLevel = fitnessLevel;
    if (fitnessGoal !== undefined) update.fitnessGoal = fitnessGoal;
    
    const user = await User.findOneAndUpdate(
      { userId: req.params.userId },
      { $set: update },
      { new: true, upsert: true, setDefaultsOnInsert: true}
    );

    res.json(user); // Return flat user object
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// PUT /api/users/:userId/settings - update user settings
router.put('/:userId/settings', async (req, res) => {
  try {
    const update = {updateAt: new Date() };
    if (language !== undefined) update.language = language;
    if (notificationsEnabled !== undefined) update.notificationsEnabled = notificationsEnabled;
    if (biometricEnabled !== undefined) update.biometricEnabled = biometricEnabled;
    if (speechEnabled !== undefined) update.speechEnabled = speechEnabled;

    const user = await User.findOneAndUpdate(
      { userId: req.params.userId },
      { $set: update },
      { new: true, upsert: true, setDefaultsOnInsert: true }
    );

    res.json(user);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;