const Reward = require('../models/Reward');

// Fetch user rewards profile
exports.getRewards = async (req, res) => {
    try {
        const { userId } = req.params;
        let rewardData = await Reward.findOne({ userId });

        if (!rewardData) {
            // Fallback generation if data doesn't exist yet
            rewardData = new Reward({
                userId,
                streakDays: 1,
                consistencyLevel: '14% weekly',
                updatedPoints: 50,
                claimableRewards: [
                    {
                        id: 'reward_first_step',
                        name: 'First Step Badge',
                        description: 'Unlocked immediately upon completing your first introductory goal.',
                        isUnlocked: true,
                        isClaimed: false,
                        iconUrl: 'ic_number_one_trophy'
                    }
                ],
                milestoneBadges: [
                    {
                        id: 'badge_champion',
                        name: 'Champion',
                        description: 'Win a challenge',
                        isUnlocked: true,
                        isClaimed: true,
                        iconUrl: 'ic_golden_trophy'
                    }
                ]
            });
            await rewardData.save();
        }

        return res.status(200).json({
            success: true,
            streakDays: rewardData.streakDays,
            consistencyLevel: rewardData.consistencyLevel,
            updatedPoints: rewardData.updatedPoints,
            claimableRewards: rewardData.claimableRewards,
            milestoneBadges: rewardData.milestoneBadges
        });

    } catch (error) {
        console.error("Get rewards error:", error);
        return res.status(500).json({ success: false, message: error.message });
    }
};

// Process claiming a reward
exports.claimReward = async (req, res) => {
    try {
        const { userId, badgeId } = req.body;

        const rewardData = await Reward.findOne({ userId });
        if (!rewardData) {
            return res.status(404).json({ success: false, message: "Reward profile not found" });
        }

        // Find and update the badge status
        const badge = rewardData.claimableRewards.id(badgeId);
        if (badge) {
            badge.isClaimed = true;
            rewardData.updatedPoints += 50;
            await rewardData.save();
        }

        // Response payload maps cleanly to your Android ClaimResponseDto
        return res.status(200).json({
            success: true,
            message: "Reward claimed successfully!",
            updatedPoints: rewardData.updatedPoints,
            updatedBadges: rewardData.claimableRewards
        });

    } catch (error) {
        console.error("Claim reward error:", error);
        return res.status(500).json({ success: false, message: error.message });
    }
};