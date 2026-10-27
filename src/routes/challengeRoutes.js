/**
 * =====================================================================================
 * ROUTE: challengeRoutes.js
 * =====================================================================================
 * GET  /api/challenges/user/:userId        The user's joined challenges with progress.
 * POST /api/challenges/:challengeId/join   Joins a challenge (idempotent-safe).
 * POST /api/challenges/:challengeId/checkin Records today's workout toward a challenge.
 * =====================================================================================
 */

const express = require('express');
const router = express.Router();
const Challenge = require('../models/Challenge');
const UserChallenge = require('../models/UserChallenge');

/**
 * Returns the timestamp of local midnight for a date. Comparing two of these tells
 * us whether two moments fall on the same calendar day.
 */
const startOfDay = d => {
  const x = new Date(d);
  x.setHours(0, 0, 0, 0);
  return x.getTime();
};

// GET /api/challenges/user/:userId
router.get('/user/:userId', async (req, res) => {
  try {
    const userChallenges = await UserChallenge.find({ userId: req.params.userId })
      .sort({ joinedAt: -1 }) // newest first
      .lean();

    // One query for all definitions, rather than one per challenge.
    const defs = await Challenge.find({
      challengeId: { $in: userChallenges.map(u => u.challengeId) },
    }).lean();
    const defById = new Map(defs.map(d => [d.challengeId, d]));

    res.status(200).json(
      userChallenges.map(u => {
        const def = defById.get(u.challengeId);
        // Falls back to 7 days if the definition was deleted, so the division below
        // can never divide by zero or undefined.
        const durationDays = def?.durationDays ?? 7;
        // Clamped so the count can't go above the programme length.
        const daysCompleted = Math.min(u.daysCompleted, durationDays);
        return {
          id: u.challengeId,            // the app uses this to build the check-in URL
          title: def?.name ?? 'Unknown challenge',
          description: def?.description ?? '',
          daysCompleted,
          durationDays,
          // A number, not a "85%" string. The app parses this as an Int, and a string
          // would make the whole list fail to parse.
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

    // Only active programmes can be joined, so retired challenges stay out of new sign-ups.
    const def = await Challenge.findOne({ challengeId, isActive: true }).lean();
    if (!def) return res.status(404).json({ error: 'Challenge not found' });

    // The explicit check gives a clear 409 message. The unique index in the model is
    // the real guarantee against duplicates if two requests race.
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

    // Both lookups are independent, so they run in parallel.
    const [uc, def] = await Promise.all([
      UserChallenge.findOne({ userId, challengeId }),
      Challenge.findOne({ challengeId }).lean(),
    ]);

    if (!uc) return res.status(404).json({ error: 'You have not joined this challenge' });
    if (uc.completed) return res.status(400).json({ error: 'Challenge already completed' });

    // One check-in per calendar day. Without this, a user could tap the button repeatedly
    // to finish a 7-day challenge in one sitting.
    if (uc.lastCheckIn && startOfDay(uc.lastCheckIn) === startOfDay(new Date())) {
      return res.status(409).json({ error: 'Already checked in today' });
    }

    uc.daysCompleted += 1;
    uc.lastCheckIn = new Date();

    // Marks the challenge complete once the final day is reached. This is what unlocks
    // the reward claim and the trophy badges.
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