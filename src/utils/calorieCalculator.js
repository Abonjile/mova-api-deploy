// Calorie calculation utilities based on the MET (Metabolic Equivalent of Task) formula.
// MET values represent the energy cost of an activity relative to resting.
// Formula: calories = MET × weight(kg) × duration(hours) × genderFactor

// Returns the work seconds per set based on exercise difficulty.
// This is the time the user is actively exercising within one set.
const getWorkSecondsPerSet = (difficulty) => {
  switch ((difficulty || 'BEGINNER').toUpperCase()) {
    case 'BEGINNER':     return 45;
    case 'INTERMEDIATE': return 30;
    case 'ADVANCED':     return 20;
    default:             return 45;
  }
};

// Returns the multiplier applied to calorie totals based on gender.
// Simplification for the prototype — real values depend on BMR.
const getGenderFactor = (gender) => {
  switch ((gender || 'MALE').toUpperCase()) {
    case 'FEMALE': return 0.9;
    case 'OTHER':  return 0.95;
    default:       return 1.0;
  }
};

// Calculates the total duration in seconds for one exercise.
// duration = sets × (workSecondsPerSet + restBetweenSets)
// The rest period is fixed at 10 seconds between sets.
const calculateExerciseDuration = (sets, difficulty) => {
  const workSeconds = getWorkSecondsPerSet(difficulty);
  const restSeconds = 10;
  return sets * workSeconds;
};

// Calculates calories burned for a single exercise.
// If the exercise was skipped (completed=false), returns 0.
const calculateExerciseCalories = (metValue, weightKg, durationSeconds, gender, completed = true) => {
  if (!completed) return 0;
  if (!metValue || !weightKg || !durationSeconds) return 0;
  const hours = durationSeconds / 3600;
  const factor = getGenderFactor(gender);
  return Math.round(metValue * weightKg * hours * factor);
};

// Legacy function — still used by the existing /api/workouts/log route.
// Kept for backward compatibility so old logs still calculate correctly.
const calculateWorkoutCalories = (exercises, weightKg) => {
  let total = 0;
  exercises.forEach(ex => {
    total += calculateExerciseCalories(
      ex.metValue || 3.8,
      weightKg,
      (ex.durationMinutes || 5) * 60,
      'MALE',
      true
    );
  });
  return total;
};

module.exports = {
  getWorkSecondsPerSet,
  getGenderFactor,
  calculateExerciseDuration,
  calculateExerciseCalories,
  calculateWorkoutCalories
};