const mongoose = require('mongoose');

const WorkoutLogSchema = new mongoose.Schema({
  workoutLogId: {
    type: String,
    required: true,
    unique: true,
    default: () => `wl_${Date.now()}_${Math.random().toString(36).substr(2, 6)}`
  },
  userId: { type: String, ref: 'User', required: true },
  workoutId: { type: String, ref: 'Workout', required: true },
  durationMinutes: { type: Number, required: true },
  caloriesBurned: { type: Number, default: 0 },
  weightAtTime: { type: Number },
  heightAtTime: { type: Number },
  ageAtTime: { type: Number },
  genderAtTime: { type: String, enum: ['MALE', 'FEMALE', 'OTHER'] },
  completed: { type: Boolean, default: true },
  userNote: { type: String, default: '' },
  isSynced: { type: Boolean, default: true },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('WorkoutLog', WorkoutLogSchema);