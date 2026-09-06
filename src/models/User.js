const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
  userId: {
    type: String,
    required: true,
    unique: true,
    default: () => `usr_${Date.now()}_${Math.random().toString(36).substr(2, 6)}`
  },
  fullName: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  ssoProvider: { type: String, enum: ['GOOGLE', 'FACEBOOK', 'APPLE'], required: true },
  ssoProviderId: { type: String, required: true },
  dateOfBirth: Date,
  gender: { type: String, enum: ['MALE', 'FEMALE', 'OTHER'] },
  height: Number,
  weight: Number,
  fitnessLevel: { type: String, enum: ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'], default: 'BEGINNER' },
  fitnessGoal: String,
  language: { type: String, enum: ['ENGLISH', 'ISIZULU', 'SOTHO'], default: 'ENGLISH' },
  notificationsEnabled: { type: Boolean, default: true },
  reminderTime: String,
  biometricEnabled: { type: Boolean, default: false },
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

// ✅ THIS MUST BE HERE — exporting the model
module.exports = mongoose.model('User', UserSchema);