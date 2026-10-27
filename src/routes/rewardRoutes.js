/**
 * =====================================================================================
 * ROUTE: rewardRoutes.js
 * =====================================================================================
 * GET  /api/rewards/:userId  Streak, weekly consistency, claimable rewards and trophies.
 * POST /api/rewards/claim    Claims the bonus for one completed challenge, exactly once.
 * =====================================================================================
 */

const express = require('express');
const mongoose = require('mongoose');
const router = express.Router();
const UserChallenge = require('../models/UserChallenge');
const Challenge = require('../models/Challenge');
const WorkoutLog = require('../models/WorkoutLog');

const DAY_MS = 86400000;
const REWARD_POINTS = 100;

/** Timestamp of local midnight, used to compare calendar days. */
const startOfDay = d => {
  const x = new Date(d);
  x.setHours(0, 0, 0, 0);
  return x.getTime();
};

/**
 * Counts consecutive workout days. The streak stays alive if the user trained today
 * or yesterday. Any missed day ends it.
 */
function currentStreak(workoutDates) {
  const days = [...new Set(workoutDates.map(startOfDay))].sort((a, b) => b - a);
  if (days.length === 0) return 0;

  const today = startOfDay(new Date());
  if (today - days[0] > DAY_MS) return 0;

  let streak = 1;
  for (let i = 1; i < days.length; i++) {
    if (days[i - 1] - days[i] !== DAY_MS) break;
    streak++;
  }
  return streak;
}

// GET /api/rewards/:userId
router.get('/:userId', async (req, res) => {
  try {
    const { userId } = req.params;

    const logs = await WorkoutLog.find({ userId, completed: true }).sort({ date: -1 }).lean();
    const streakDays = currentStreak(logs.map(l => l.date));

    // Consistency: share of the last seven days (today included) with a workout.
    const weekStart = new Date(Date.now() - 6 * DAY_MS);
    const activeDaysThisWeek = new Set(
      logs.filter(l => l.date >= weekStart).map(l => startOfDay(l.date))
    ).size;
    const consistencyLevel = Math.round((activeDaysThisWeek / 7) * 100);

    // Challenge durations live on the Challenge definition, not on UserChallenge.
    const userChallenges = await UserChallenge.find({ userId }).lean();
    const defs = await Challenge.find({
      challengeId: { $in: userChallenges.map(u => u.challengeId) },
    }).lean();
    const defById = new Map(defs.map(d => [d.challengeId, d]));

    const completed = userChallenges.filter(u => u.completed);

    const claimableRewards = completed.map(u => ({
      id: String(u._id),
      title: `${defById.get(u.challengeId)?.name ?? 'Challenge'} Complete`,
      description: `Claim +${REWARD_POINTS} bonus FitPoints`,
      isClaimed: !!u.rewardClaimed,
    }));

    // Trophies use >= so finishing a 10-day challenge also unlocks the 4 and 7-day trophies.
    const completedDurations = completed.map(u => defById.get(u.challengeId)?.durationDays ?? 0);
    const reached = n => completedDurations.some(d => d >= n);

    // Your trophy names. The app reads "title" and "subtitle", so the field names match the DTO.
    const milestoneBadges = [
      { title: 'Champion', subtitle: 'Win a challenge', isUnlocked: completed.length > 0 },
      { title: '4-Day Challenge Trophy', subtitle: 'Complete a 4-Day Challenge', isUnlocked: reached(4) },
      { title: '7-Day Challenge Trophy', subtitle: 'Complete a 7-Day Challenge', isUnlocked: reached(7) },
      { title: '10-Day Challenge Trophy', subtitle: 'Complete a 10-Day Challenge', isUnlocked: reached(10) },
    ];

    res.status(200).json({ userId, streakDays, consistencyLevel, claimableRewards, milestoneBadges });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// POST /api/rewards/claim  body: { userId, rewardId }
router.post('/claim', async (req, res) => {
  try {
    const { userId, rewardId } = req.body;

    if (!userId || !mongoose.isValidObjectId(rewardId)) {
      return res.status(400).json({ error: 'userId and a valid rewardId are required' });
    }

    const uc = await UserChallenge.findOne({ _id: rewardId, userId });
    if (!uc) return res.status(404).json({ error: 'Reward not found' });
    if (!uc.completed) return res.status(400).json({ error: 'Challenge not completed yet' });
    if (uc.rewardClaimed) return res.status(409).json({ error: 'Reward already claimed' });

    uc.rewardClaimed = true;
    uc.rewardClaimedAt = new Date();
    await uc.save();

    res.status(200).json({ message: 'Reward claimed', pointsAwarded: REWARD_POINTS });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;