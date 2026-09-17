const mongoose = require('mongoose');

const WorkoutSchema = new mongoose.Schema({
  workoutId: {
    type: String,
    required: true,
    unique: true,
    default: () => `wk_${Date.now()}_${Math.random().toString(36).substr(2, 6)}`
  },
  userId: {
    type: String,
    ref: 'User',
    required: true
  },
  name: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  difficulty: {
    type: String,
    enum: ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'],
    default: 'BEGINNER'
  },
  durationMinutes: {
    type: Number,
    default: 20
  },
  exercises: [{
    exerciseId: { type: String, ref: 'Exercise' },
    sets: { type: Number, default: 3 },
    reps: { type: Number, default: 12 },
    restSeconds: { type: Number, default: 30 }
  }],
  isCustom: {
    type: Boolean,
    default: false
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

module.exports = mongoose.model('Workout', WorkoutSchema);