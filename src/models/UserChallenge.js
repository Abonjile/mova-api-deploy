const mongoose = require('mongoose');

const UserChallengeSchema = new mongoose.Schema({
  userChallengeId: {
    type: String,
    required: true,
    unique: true,
    default: () => `uc_${Date.now()}_${Math.random().toString(36).substr(2, 6)}`
  },
  userId: {
    type: String,
    ref: 'User',
    required: true
  },
  challengeId: {
    type: String,
    ref: 'Challenge',
    required: true
  },
  startDate: {
    type: Date,
    default: Date.now
  },
  endDate: {
    type: Date
  },
  currentDay: {
    type: Number,
    default: 0
  },
  streakCount: {
    type: Number,
    default: 0
  },
  completed: {
    type: Boolean,
    default: false
  },
  customNote: {
    type: String,
    default: ''
  },
  badgeEarned: {
    type: Boolean,
    default: false
  },
  completedAt: {
    type: Date
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('UserChallenge', UserChallengeSchema);