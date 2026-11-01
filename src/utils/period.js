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
 * Builds the time window for a period, ending now and starting at local midnight
 * (days - 1) days ago. Starting at midnight means "today" is always a full day,
 * not a partial one.
 */
function getPeriodRange(period) {
  const { days, buckets } = PERIODS[normalisePeriod(period)];
  const start = new Date();
  start.setHours(0, 0, 0, 0);
  start.setDate(start.getDate() - (days - 1));
  return { start, end: new Date(), days, buckets };
}

/**
 * Maps a date to a chart bucket index. Index 0 is the oldest bucket and the last
 * index is the most recent, which matches the order the app draws the charts in.
 * The result is clamped so a log that falls exactly on the boundary can't produce
 * an out-of-range index.
 */
function bucketIndex(date, { start, days, buckets }) {
  const dayOffset = Math.floor((new Date(date) - start) / 86400000); // ms per day
  const size = days / buckets; // 1 day per bucket for Weekly, 7 days for Monthly
  return Math.min(Math.max(Math.floor(dayOffset / size), 0), buckets - 1);
}

module.exports = { normalisePeriod, getPeriodRange, bucketIndex };