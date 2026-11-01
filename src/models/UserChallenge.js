/**
 * =====================================================================================
 * MODEL: UserChallenge
 * =====================================================================================
 * Records one user's progress through one challenge. Progress lives here, not on the
 * shared Challenge definition, so every user has independent progress on the same
 * programme.
 * =====================================================================================
 */

const mongoose = require('mongoose');

const UserChallengeSchema = new mongoose.Schema({
  userId: { 
    type: String, 
    required: true, 
    index: true 
  },
  challengeId: { 
    type: String, 
    required: true 
  },
  // Progress is stored as a count of completed days, not as a percentage. The
  // percentage is derived when the data is read, so it can't drift out of sync.
  daysCompleted: { 
    type: Number, 
    default: 0, 
    min: 0 },
  // Used to allow only one check-in per calendar day.
  lastCheckIn: Date,
  completed: { 
    type: Boolean, 
    default: false
   },
  completedAt: Date,
  // Reward state is tracked per challenge, so claiming one reward never affects another.
  rewardClaimed: { 
    type: Boolean, 
    default: false
   },
  rewardClaimedAt: Date,
  joinedAt: { 
    type: Date, 
    default: 
    Date.now },
});

/* A user can join a given challenge only once. The database enforces this, so it
 holds even when two requests arrive at the same moment. */
UserChallengeSchema.index(
  { userId: 1, 
  challengeId: 1 
},
{ 
  unique: true 
});

module.exports = mongoose.model('UserChallenge', UserChallengeSchema);