const mongoose = require('mongoose');

const RewardSchema = new mongoose.Schema({
    userId: { type: String, required: true, unique: true },
    streakDays: { type: Number, default: 5 },
    badgesEarned: { type: Number, default: 2 },
    rewardTitle: { type: String, default: '7-Day Jumpstart Complete' },
    isClaimed: { type: Boolean, default: false }
});

module.exports = mongoose.model('Reward', RewardSchema);