/**
 * =====================================================================================
 * ROUTE: rewardRoutes.js
 * =====================================================================================
 * Backend Express router managing user reward statistics, streak tracking, claimable
 * challenge rewards, and 4, 7, and 10-day challenge completion trophy milestone badges.
 * =====================================================================================
 */

const express = require('express');
const router = express.Router();
const UserChallenge = require('../models/UserChallenge');
const WorkoutLog = require('../models/WorkoutLog');

/**
 * GET /api/rewards/:userId
 * Retrieves user workout logs and completed challenges to calculate real streaks,
 * consistency level, claimable rewards, and 4, 7, and 10-day challenge trophies.
 */
router.get('/:userId', async (req, res) => {
    try {
        const { userId } = req.params;

        // 1. Calculate real streak and consistency from MongoDB workout logs
        const logs = await WorkoutLog.find({ userId, completed: true }).sort({ date: -1 });
        const streakDays = logs.length > 0 ? (logs[0].streakCount || logs.length) : 0;
        const consistencyLevel = logs.length > 0 ? Math.min(Math.round((logs.length / 7) * 100), 100) : 0;

        // 2. Fetch completed challenges to generate claimable rewards
        const completedChallenges = await UserChallenge.find({ userId, completed: true });
        const claimableRewards = completedChallenges.map(uc => ({
            rewardId: uc.userChallengeId,
            title: `${uc.challengeName || 'Challenge'} Complete`,
            description: 'Claim +100 bonus FitPoints',
            isClaimed: uc.rewardClaimed || false
        }));

        // 3. Evaluate 4, 7, and 10-Day Challenge Completion Trophies
        const maxCompletedDuration = completedChallenges.reduce((max, uc) => Math.max(max, uc.durationDays || 0), 0);

        const milestoneBadges = [
            {
                badgeId: 'trophy_4',
                name: '4-Day Challenge Trophy',
                description: 'Complete a 4-Day Challenge',
                isUnlocked: maxCompletedDuration >= 4 || completedChallenges.some(uc => (uc.durationDays || 0) === 4)
            },
            {
                badgeId: 'trophy_7',
                name: '7-Day Challenge Trophy',
                description: 'Complete a 7-Day Challenge',
                isUnlocked: maxCompletedDuration >= 7 || completedChallenges.some(uc => (uc.durationDays || 0) === 7)
            },
            {
                badgeId: 'trophy_10',
                name: '10-Day Challenge Trophy',
                description: 'Complete a 10-Day Challenge',
                isUnlocked: maxCompletedDuration >= 10 || completedChallenges.some(uc => (uc.durationDays || 0) === 10)
            }
        ];

        res.status(200).json({
            userId,
            streakDays,
            consistencyLevel,
            claimableRewards,
            milestoneBadges
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

/**
 * POST /api/rewards/claim
 * Marks a specific challenge reward as claimed for the authenticated user.
 */
router.post('/claim', async (req, res) => {
    try {
        const { userId, rewardId } = req.body;
        await UserChallenge.findOneAndUpdate(
            { userChallengeId: rewardId, userId },
            { rewardClaimed: true },
            { new: true }
        );
        res.status(200).json({ message: 'Reward successfully claimed' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;