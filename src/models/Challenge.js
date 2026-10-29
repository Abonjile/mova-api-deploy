const mongoose = require('mongoose');

const ChallengeSchema = new mongoose.Schema({
    challengeId: { type: String, required: true, unique: true },
    name: { type: String, required: true },
    description: { type: String, required: true },
    durationDays: { type: Number, required: true },
    difficulty: { type: String, default: 'BEGINNER' },
    isActive: { type: Boolean, default: true },
    currentDay: { type: Number, default: 1 },
    totalDays: { type: Number, default: 7 },
    completionPercentage: { type: String, default: '0%' },
    userId: { type: String, required: true }
});

module.exports = mongoose.model('Challenge', ChallengeSchema);