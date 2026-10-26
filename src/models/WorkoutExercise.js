const mongoose = require('mongoose');

const WorkoutExerciseSchema = new mongoose.Schema({
  workoutExerciseId: {
    type: String,
    required: true,
    unique: true,
    default: () => `we_${Date.now()}_${Math.random().toString(36).substr(2, 6)}`
  },
  workoutId: { type: String, ref: 'Workout', required: true },
  exerciseId: { type: String, ref: 'Exercise', required: true },
  orderNumber: { type: Number, default: 1 },
  sets: { type: Number, default: 3 },
  reps: { type: Number, default: 12 },
  restSeconds: { type: Number, default: 30 }
});

module.exports = mongoose.model('WorkoutExercise', WorkoutExerciseSchema);