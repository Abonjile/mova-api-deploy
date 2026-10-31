/**
 * =====================================================================================
 * SERVICE: challengeProgress.js
 * =====================================================================================
 * Moves a user's joined challenges forward when they complete a workout. Each challenge
 * advances at most one day per calendar day, so logging several workouts in one day
 * only counts once.
 * =====================================================================================
 */

const UserChallenge = require('../models/UserChallenge');
const Challenge = require('../models/Challenge');

/** True when two dates fall on the same calendar day. */
function sameDay(a, b) {
  const x = new Date(a);
  const y = new Date(b);
  return x.getFullYear() === y.getFullYear()
    && x.getMonth() === y.getMonth()
    && x.getDate() === y.getDate();
}

/**
 * Adds one completed day to every unfinished challenge the user has joined.
 * Returns how many challenges moved forward.
 */
async function recordWorkoutDay(userId, when = new Date()) {
  const active = await UserChallenge.find({ userId, completed: false });
  if (active.length === 0) return 0;

  const defs = await Challenge.find({
    challengeId: { $in: active.map(u => u.challengeId) },
  }).lean();
  const durationById = new Map(defs.map(d => [d.challengeId, d.durationDays]));

  let updated = 0;
  for (const uc of active) {
    // Already counted today, so skip it.
    if (uc.lastCheckIn && sameDay(uc.lastCheckIn, when)) continue;

    uc.daysCompleted += 1;
    uc.lastCheckIn = when;

    const duration = durationById.get(uc.challengeId) ?? 7;
    if (uc.daysCompleted >= duration) {
      uc.completed = true;
      uc.completedAt = when;
    }

    await uc.save();
    updated += 1;
  }
  return updated;
}

module.exports = { recordWorkoutDay, sameDay };