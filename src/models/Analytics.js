/**
 * =====================================================================================
 * MODEL: Analytics
 * =====================================================================================
 * Stores only the user's goal for a given period. Burned calories, trends and the
 * muscle distribution are calculated from workout logs on every request. Storing
 * them caused the stale and empty chart bugs, so they are intentionally not stored.
 * =====================================================================================
 */

const mongoose = require('mongoose');

const AnalyticsSchema = new mongoose.Schema({
  userId: { type: String, required: true },
  // Goals are kept separately for Weekly and Monthly, so changing one doesn't affect the other.
  period: { type: String, enum: ['Weekly', 'Monthly'], default: 'Weekly' },
  goalTitle: { type: String, default: 'Burn 2000 cal this week' },
  // Lower bound of 100 stops a typo like 0 from producing a divide-by-zero progress bar.
  targetCalories: { type: Number, default: 2000, min: 100 },
  updatedAt: { type: Date, default: Date.now },
});

// One goal per user per period. This also makes concurrent upserts safe.
AnalyticsSchema.index({ userId: 1, period: 1 }, { unique: true });

module.exports = mongoose.model('Analytics', AnalyticsSchema);