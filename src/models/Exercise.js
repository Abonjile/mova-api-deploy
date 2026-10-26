const mongoose = require('mongoose');

const ExerciseSchema = new mongoose.Schema({
  exerciseId: {
    type: String,
    required: true,
    unique: true,
    default: () => `ex_${Date.now()}_${Math.random().toString(36).substr(2, 6)}`
  },
  name: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  targetMuscleGroup: {
    type: String,
    required: true
  },
  difficulty: {
    type: String,
    enum: ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'],
    default: 'BEGINNER'
  },
  videoUrl: {
    type: String,
    default: ''
  },
  metValue: {
    type: Number,
    default: 3.8,
    description: 'Metabolic Equivalent of Task for calorie calculation'
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

module.exports = mongoose.model('Exercise', ExerciseSchema);