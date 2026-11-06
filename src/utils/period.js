/**
 * =====================================================================================
 * UTILITY: period.js
 * =====================================================================================
 * Shared definitions for the Weekly and Monthly analytics windows. Both the analytics
 * and rewards routes use this file so the two screens always agree on what "this week"
 * means.
 * =====================================================================================
 */

// Each period defines how many days it covers and how many chart buckets it produces.
// Weekly: one bar per day (7 bars). Monthly: one bar per week over 4 weeks (28 days).
const PERIODS = {
  Weekly:  { days: 7,  buckets: 7 },
  Monthly: { days: 28, buckets: 4 },
};

/**
 * Returns a valid period name. Anything unknown falls back to "Weekly" so a bad
 * query parameter can't break the route.
 *
 * Uses hasOwnProperty rather than a plain lookup, because PERIODS["constructor"]
 * would otherwise return a truthy function.
 */
function normalisePeriod(p) {
  return Object.prototype.hasOwnProperty.call(PERIODS, p) ? p : 'Weekly';
}

/**
 * Builds the time window for a period, ending now and starting at UTC midnight
 * (days - 1) days ago. Adding one day of padding prevents timezone differences
 * (e.g., South Africa UTC+2 vs Render's UTC) from dropping recent workouts.
 */
function getPeriodRange(period) {
  const { days, buckets } = PERIODS[normalisePeriod(period)];
  const start = new Date();
  start.setUTCHours(0, 0, 0, 0);
  start.setUTCDate(start.getUTCDate() - (days - 1) - 1); // -1 day padding for timezone
  return { start, end: new Date(), days: days + 1, buckets }; // extra day in range
}

/**
 * Maps a date to a chart bucket index, clamped so it never goes out of range.
 */
function bucketIndex(date, { start, days, buckets }) {
  const dayOffset = Math.floor((new Date(date) - start) / 86400000); // ms per day
  const size = days / buckets; // 1 day per bucket for Weekly, 7 for Monthly
  return Math.min(Math.max(Math.floor(dayOffset / size), 0), buckets - 1);
}

module.exports = { normalisePeriod, getPeriodRange, bucketIndex };