/**
 * Calculate calories burned using MET formula
 * @param {number} metValue - MET value of the exercise
 * @param {number} weightKg - User's weight in kg
 * @param {number} durationMinutes - Duration in minutes
 * @returns {number} calories burned
 */
const calculateCalories = (metValue, weightKg, durationMinutes) => {
  const durationHours = durationMinutes / 60;
  const calories = metValue * weightKg * durationHours;
  return Math.round(calories);
};

/**
 * Calculate calories for a full workout (multiple exercises)
 * @param {Array} exercises - Array of { metValue, durationMinutes }
 * @param {number} weightKg - User's weight
 * @returns {number} total calories
 */
const calculateWorkoutCalories = (exercises, weightKg) => {
  let total = 0;
  exercises.forEach(ex => {
    total += calculateCalories(ex.metValue || 3.8, weightKg, ex.durationMinutes || 5);
  });
  return Math.round(total);
};

module.exports = { calculateCalories, calculateWorkoutCalories };