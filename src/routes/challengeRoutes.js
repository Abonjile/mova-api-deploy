/**
 * ROUTE: challengeRoutes.js
 * GET  /api/challenges/catalogue/:userId     Every active programme, marked joined or not.
 * GET  /api/challenges/user/:userId          The programmes this user has joined, with progress.
 * POST /api/challenges/:challengeId/join     Joins a programme (one join per user).
 * POST /api/challenges/:challengeId/checkin  Manual check-in for today (once per day).
 */

const express = require('express');
const router = express.Router();
const Challenge = require('../models/Challenge');
const UserChallenge = require('../models/UserChallenge');
const { sameDay } = require('../services/challengeProgress');

// GET /api/challenges/catalogue/:userId
router.get('/catalogue/:userId', async (req, res) => {
  try {
    const defs = await Challenge.find({ isActive: true }).sort({ durationDays: 1 }).lean();
    const joined = await UserChallenge.find({ userId: req.params.userId }).lean();
    const joinedById = new Map(joined.map(u => [u.challengeId, u]));

    res.status(200).json(
      defs.map(d => {
        const uc = joinedById.get(d.challengeId);
        return {
          id: d.challengeId,
          title: d.name,
          description: d.description,
          durationDays: d.durationDays,
          difficulty: d.difficulty,
          joined: !!uc,
          completed: !!uc?.completed,
        };
      })
    );
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// GET /api/challenges/user/:userId
router.get('/user/:userId', async (req, res) => {
  try {
    const userChallenges = await UserChallenge.find({ userId: req.params.userId })
      .sort({ joinedAt: -1 })
      .lean();

    const defs = await Challenge.find({
      challengeId: { $in: userChallenges.map(u => u.challengeId) },
    }).lean();
    const defById = new Map(defs.map(d => [d.challengeId, d]));

    res.status(200).json(
      userChallenges.map(u => {
        const def = defById.get(u.challengeId);
        const durationDays = def?.durationDays ?? 7;
        const daysCompleted = Math.min(u.daysCompleted, durationDays);
        return {
          id: u.challengeId,
          title: def?.name ?? 'Unknown challenge',
          description: def?.description ?? '',
          daysCompleted,
          durationDays,
          progressPercentage: Math.round((daysCompleted / durationDays) * 100),
          completed: u.completed,
        };
      })
    );
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/challenges/:challengeId/join  body: { userId }
router.post('/:challengeId/join', async (req, res) => {
  try {
    const { userId } = req.body;
    const { challengeId } = req.params;
    if (!userId) return res.status(400).json({ error: 'userId is required' });

    const def = await Challenge.findOne({ challengeId, isActive: true }).lean();
    if (!def) return res.status(404).json({ error: 'Challenge not found' });

    if (await UserChallenge.exists({ userId, challengeId })) {
      return res.status(409).json({ error: 'Already joined' });
    }

    await UserChallenge.create({ userId, challengeId });
    res.status(201).json({ message: 'Joined challenge', challengeId });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/challenges/:challengeId/checkin  body: { userId }
router.post('/:challengeId/checkin', async (req, res) => {
  try {
    const { userId } = req.body;
    const { challengeId } = req.params;

    const [uc, def] = await Promise.all([
      UserChallenge.findOne({ userId, challengeId }),
      Challenge.findOne({ challengeId }).lean(),
    ]);

    if (!uc) return res.status(404).json({ error: 'You have not joined this challenge' });
    if (uc.completed) return res.status(400).json({ error: 'Challenge already completed' });
    if (uc.lastCheckIn && sameDay(uc.lastCheckIn, new Date())) {
      return res.status(409).json({ error: 'Already checked in today' });
    }

    uc.daysCompleted += 1;
    uc.lastCheckIn = new Date();

    const duration = def?.durationDays ?? 7;
    if (uc.daysCompleted >= duration) {
      uc.completed = true;
      uc.completedAt = new Date();
    }

    await uc.save();
    res.status(200).json({
      message: 'Checked in',
      daysCompleted: uc.daysCompleted,
      completed: uc.completed,
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router; 