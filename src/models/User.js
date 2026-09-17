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

  // SSO is optional for now so prototype testing works
  ssoProvider: { type: String, enum: ['GOOGLE', 'FACEBOOK', 'APPLE'], required: true },
  ssoProviderId: { type: String, required: true, unique: true },

  // Profile fields
  age: {type: Number},
  gender: { type: String, enum: ['Male', 'Female', 'Other'] },
  height: {type: Number},
  weight: {weight:Number},
  fitnessLevel: { type: String, enum: ['Beginner', 'Intermediate', 'Advanced', 'Athlete'], default: 'Beginner' },
  fitnessGoal: {type: String},
  location: { type: String },

  // Settings fields
  language: { type: String, enum: ['English', 'IsiZulu', 'Sesotho'], default: 'English' },
  notificationsEnabled: { type: Boolean, default: true },
  biometricEnabled: { type: Boolean, default: false },
  speechEnabled: { type: Boolean, default: false },
  reminderTime: { type: String },


  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

// Exports the model
module.exports = mongoose.model('User', UserSchema);