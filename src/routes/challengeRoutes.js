const express = require('express');
const router = express.Router();
const Challenge = require('../models/Challenge');
const UserChallenge = require('../models/UserChallenge');

// GET /api/challenges — Get all available challenges
router.get('/', async (req, res) => {
  try {
    const challenges = await Challenge.find({ isActive: true });
    res.json(challenges);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// GET /api/challenges/:id — Get challenge details
router.get('/:id', async (req, res) => {
  try {
    const challenge = await Challenge.findOne({ challengeId: req.params.id });
    if (!challenge) {
      return res.status(404).json({ message: 'Challenge not found' });
    }
    res.json(challenge);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/challenges/:id/join — Join a challenge
router.post('/:id/join', async (req, res) => {
  try {
    const { userId } = req.body;
    if (!userId) {
      return res.status(400).json({ message: 'userId is required' });
    }
    
    const challenge = await Challenge.findOne({ challengeId: req.params.id });
    if (!challenge) {
      return res.status(404).json({ message: 'Challenge not found' });
    }
    
    // Check if user already joined
    const existing = await UserChallenge.findOne({ userId, challengeId: challenge.challengeId });
    if (existing) {
      return res.status(400).json({ message: 'User already joined this challenge' });
    }
    
    // Calculate end date
    const endDate = new Date();
    endDate.setDate(endDate.getDate() + challenge.durationDays);
    
    const userChallenge = new UserChallenge({
      userId,
      challengeId: challenge.challengeId,
      endDate,
      currentDay: 0,
      streakCount: 0
    });
    
    await userChallenge.save();
    
    res.status(201).json({
      message: 'Successfully joined challenge',
      userChallenge
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// GET /api/challenges/:id/progress — Get user's challenge progress
router.get('/:id/progress', async (req, res) => {
  try {
    const { userId } = req.query;
    if (!userId) {
      return res.status(400).json({ message: 'userId is required' });
    }
    
    const userChallenge = await UserChallenge.findOne({
      userId,
      challengeId: req.params.id
    });
    
    if (!userChallenge) {
      return res.status(404).json({ message: 'User has not joined this challenge' });
    }
    
    res.json(userChallenge);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// PUT /api/challenges/:id/complete — Complete a challenge (award streak & badge)
router.put('/:id/complete', async (req, res) => {
  try {
    const { userId } = req.body;
    if (!userId) {
      return res.status(400).json({ message: 'userId is required' });
    }
    
    const userChallenge = await UserChallenge.findOne({
      userId,
      challengeId: req.params.id
    });
    
    if (!userChallenge) {
      return res.status(404).json({ message: 'User has not joined this challenge' });
    }
    
    if (userChallenge.completed) {
      return res.status(400).json({ message: 'Challenge already completed' });
    }
    
    // Update challenge completion
    userChallenge.completed = true;
    userChallenge.completedAt = new Date();
    userChallenge.streakCount += 1;
    userChallenge.badgeEarned = true;
    
    await userChallenge.save();
    
    res.json({
      message: 'Challenge completed! 🎉',
      streakCount: userChallenge.streakCount,
      badgeEarned: userChallenge.badgeEarned
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// PUT /api/challenges/:id/note — Add custom note to challenge
router.put('/:id/note', async (req, res) => {
  try {
    const { userId, customNote } = req.body;
    if (!userId || !customNote) {
      return res.status(400).json({ message: 'userId and customNote are required' });
    }
    
    const userChallenge = await UserChallenge.findOne({
      userId,
      challengeId: req.params.id
    });
    
    if (!userChallenge) {
      return res.status(404).json({ message: 'User has not joined this challenge' });
    }
    
    userChallenge.customNote = customNote;
    userChallenge.updatedAt = new Date();
    await userChallenge.save();
    
    res.json({
      message: 'Note added successfully',
      customNote: userChallenge.customNote
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;