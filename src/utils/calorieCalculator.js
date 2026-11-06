// Calorie calculation utilities based on the MET (Metabolic Equivalent of Task) formula.

// Returns the work seconds per set based on exercise difficulty.
const getWorkSecondsPerSet = (difficulty) => {
  switch ((difficulty || 'BEGINNER').toUpperCase()) {
    case 'BEGINNER':     return 45;
    case 'INTERMEDIATE': return 30;
    case 'ADVANCED':     return 20;
    default:             return 45;
  }
};

// Returns the multiplier applied to calorie totals based on gender.
const getGenderFactor = (gender) => {
  switch ((gender || 'MALE').toUpperCase()) {
    case 'FEMALE': return 0.9;
    case 'OTHER':  return 0.95;
    default:       return 1.0;
  }
};

// NEW FUNCTION: Calculates the total active duration in seconds for one exercise.
const calculateExerciseDuration = (sets, difficulty) => {
  const workSeconds = getWorkSecondsPerSet(difficulty);
  return sets * workSeconds;
};

// NEW FUNCTION: Calculates calories burned for a single exercise.
const calculateExerciseCalories = (metValue, weightKg, durationSeconds, gender, completed = true) => {
  if (!completed) return 0;
  if (!metValue || !weightKg || !durationSeconds) return 0;
  const hours = durationSeconds / 3600;
  const factor = getGenderFactor(gender);
  return Math.round(metValue * weightKg * hours * factor);
};

// Legacy function — still used by the old /log route.
const calculateCalories = (metValue, weightKg, durationMinutes) => {
  const durationHours = durationMinutes / 60;
  const calories = metValue * weightKg * durationHours;
  return Math.round(calories);
};

// Legacy function for a full workout.
const calculateWorkoutCalories = (exercises, weightKg) => {
  let total = 0;
  exercises.forEach(ex => {
    total += calculateCalories(ex.metValue || 3.8, weightKg, ex.durationMinutes || 5);
  });
  return Math.round(total);
};

module.exports = {
  getWorkSecondsPerSet,
  getGenderFactor,
  calculateExerciseDuration,
  calculateExerciseCalories,
  calculateCalories,
  calculateWorkoutCalories
};