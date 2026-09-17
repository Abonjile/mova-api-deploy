const express = require('express');
const router = express.Router();
const User = require('../models/User');

// POST /api/auth/register
router.post('/register', async (req, res) => {
  try {
    const { fullName, email, ssoProvider, ssoProviderId } = req.body;
    
    // Check if user already exists
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: 'User already exists' });
    }
    
    // Create new user
    const user = new User({
      fullName,
      email,
      ssoProvider,
      ssoProviderId
    });
    
    await user.save();
    
    res.status(201).json({
      message: 'User registered successfully',
      user: {
        userId: user.userId,
        fullName: user.fullName,
        email: user.email
      }
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
  try {
    const { email, ssoProviderId } = req.body;
    
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }
    
    // For now, just return the user
    res.json({
      message: 'Login successful',
      user: {
        userId: user.userId,
        fullName: user.fullName,
        email: user.email,
        weight: user.weight,
        height: user.height,
        fitnessLevel: user.fitnessLevel
      }
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;