const mongoose = require('mongoose');

const ChallengeSchema = new mongoose.Schema({
  challengeId: { type: String, required: true, unique: true },
  name: { type: String, required: true },
  description: { type: String, required: true },
  durationDays: { type: Number, required: true, min: 1 },
  difficulty: {
    type: String,
    enum: ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'],
    default: 'BEGINNER',
  },
  isActive: { type: Boolean, default: true },
});

module.exports = mongoose.model('Challenge', ChallengeSchema);