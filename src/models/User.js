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

   // Hashed with bcrypt. Null for SSO-only users.
  password: { type: String, default: null },

  // SSO is optional for now so prototype testing works
  ssoProvider: { type: String, enum: ['GOOGLE', 'FACEBOOK', 'APPLE', null] },
  ssoProviderId: { type: String, unique: true, sparse: true },

  // Profile fields
  age: {type: Number},
  gender: { type: String, enum: ['Male', 'Female', 'Other', 'Prefer not to say'] },
  height: {type: Number},
  weight: {type: Number},
  fitnessLevel: { type: String, enum: ['Beginner', 'Intermediate', 'Advanced', 'Athlete'], default: 'Beginner' },
  fitnessGoal: {type: String},
  objectives: { type: [String], default: [] },
  location: { type: String },

   // Settings fields
  language: { type: String, enum: ['English', 'isiZulu', 'Sesotho'], default: 'English' },
  notificationsEnabled: { type: Boolean, default: true },
  biometricEnabled: { type: Boolean, default: false },
  speechEnabled: { type: Boolean, default: false },
  reminderTime: { type: String },
  
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

// Exports the model
module.exports = mongoose.model('User', UserSchema);