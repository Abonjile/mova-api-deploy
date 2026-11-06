const mongoose = require('mongoose');

const badgeSchema = new mongoose.Schema({
    id: { type: String, required: true },
    name: { type: String, required: true },
    description: { type: String, required: true },
    isUnlocked: { type: Boolean, default: false },
    isClaimed: { type: Boolean, default: false },
    iconUrl: { type: String, default: 'ic_red_medal' }
});

const rewardSchema = new mongoose.Schema({
    userId: { type: String, required: true, unique: true },
    streakDays: { type: Number, default: 1 },
    consistencyLevel: { type: String, default: '14% weekly' },
    updatedPoints: { type: Number, default: 100 },
    claimableRewards: [badgeSchema],
    milestoneBadges: [badgeSchema]
});

module.exports = mongoose.model('Reward', rewardSchema);