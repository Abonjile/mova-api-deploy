const mongoose = require('mongoose');

const ChallengeSchema = new mongoose.Schema({
  challengeId: {
    type: String,
    required: true,
    unique: true,
    default: () => `ch_${Date.now()}_${Math.random().toString(36).substr(2, 6)}`
  },
  name: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  durationDays: {
    type: Number,
    enum: [7, 10, 30],
    required: true
  },
  difficulty: {
    type: String,
    enum: ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'],
    default: 'BEGINNER'
  },
  // Reference to the workout template for this challenge
  workoutId: {
    type: String,
    ref: 'Workout'
  },
  isActive: {
    type: Boolean,
    default: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('Challenge', ChallengeSchema);