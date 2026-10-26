require('dotenv').config();
const mongoose = require('mongoose');
const Exercise = require('./src/models/Exercise');
const Workout = require('./src/models/Workout');
const WorkoutExercise = require('./src/models/WorkoutExercise');

// ─────────────────────────────────────────────
// 16 EXERCISES
// ─────────────────────────────────────────────
const exercises = [
  { name: 'Push-ups', description: 'Standard push-up', targetMuscleGroup: 'Chest', difficulty: 'BEGINNER', metValue: 4.2, defaultSets: 3, defaultReps: 12, videoUrl: '' },
  { name: 'Squats', description: 'Bodyweight squat', targetMuscleGroup: 'Legs', difficulty: 'BEGINNER', metValue: 5.5, defaultSets: 3, defaultReps: 15, videoUrl: '' },
  { name: 'Plank', description: 'Hold plank position', targetMuscleGroup: 'Core', difficulty: 'INTERMEDIATE', metValue: 3.8, defaultSets: 3, defaultReps: 30, videoUrl: '' },
  { name: 'Lunges', description: 'Forward lunge', targetMuscleGroup: 'Legs', difficulty: 'INTERMEDIATE', metValue: 4.5, defaultSets: 3, defaultReps: 10, videoUrl: '' },
  { name: 'Burpees', description: 'Full body explosive movement', targetMuscleGroup: 'Full Body', difficulty: 'ADVANCED', metValue: 10.0, defaultSets: 3, defaultReps: 10, videoUrl: '' },
  { name: 'Jumping Jacks', description: 'Cardio warm-up', targetMuscleGroup: 'Full Body', difficulty: 'BEGINNER', metValue: 7.0, defaultSets: 3, defaultReps: 20, videoUrl: '' },
  { name: 'Mountain Climbers', description: 'Dynamic core exercise', targetMuscleGroup: 'Core', difficulty: 'INTERMEDIATE', metValue: 8.0, defaultSets: 3, defaultReps: 20, videoUrl: '' },
  { name: 'Glute Bridges', description: 'Hip raise for glutes', targetMuscleGroup: 'Glutes', difficulty: 'BEGINNER', metValue: 4.0, defaultSets: 3, defaultReps: 15, videoUrl: '' },
  { name: 'Donkey Kicks', description: 'Quadruped hip extension', targetMuscleGroup: 'Glutes', difficulty: 'BEGINNER', metValue: 4.5, defaultSets: 3, defaultReps: 12, videoUrl: '' },
  { name: 'Bicycle Crunches', description: 'Alternating ab crunch', targetMuscleGroup: 'Core', difficulty: 'INTERMEDIATE', metValue: 4.0, defaultSets: 3, defaultReps: 20, videoUrl: '' },
  { name: 'Russian Twists', description: 'Seated rotational core', targetMuscleGroup: 'Core', difficulty: 'INTERMEDIATE', metValue: 4.0, defaultSets: 3, defaultReps: 20, videoUrl: '' },
  { name: 'Tricep Dips', description: 'Chair tricep dip', targetMuscleGroup: 'Arms', difficulty: 'INTERMEDIATE', metValue: 5.0, defaultSets: 3, defaultReps: 12, videoUrl: '' },
  { name: 'Bicep Curls', description: 'Dumbbell curl', targetMuscleGroup: 'Arms', difficulty: 'BEGINNER', metValue: 3.5, defaultSets: 3, defaultReps: 12, videoUrl: '' },
  { name: 'Shoulder Taps', description: 'Plank shoulder taps', targetMuscleGroup: 'Shoulders', difficulty: 'INTERMEDIATE', metValue: 4.0, defaultSets: 3, defaultReps: 20, videoUrl: '' },
  { name: 'High Knees', description: 'Running in place', targetMuscleGroup: 'Cardio', difficulty: 'BEGINNER', metValue: 8.0, defaultSets: 3, defaultReps: 30, videoUrl: '' },
  { name: 'Wall Sit', description: 'Isometric wall hold', targetMuscleGroup: 'Legs', difficulty: 'INTERMEDIATE', metValue: 4.5, defaultSets: 3, defaultReps: 45, videoUrl: '' }
];

// ─────────────────────────────────────────────
// 12 WORKOUTS (each links to exercises)
// ─────────────────────────────────────────────
// Exercises are referenced by NAME here. The script matches them to real exerciseIds after insertion.
const workouts = [
  {
    name: 'Abs Workout',
    description: 'Core-focused routine',
    difficulty: 'BEGINNER',
    durationMinutes: 10,
    imageRes: 'abs_workout',
    exercises: [
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Bicycle Crunches', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Russian Twists', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Bicycle Crunches', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Russian Twists', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Bicycle Crunches', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Russian Twists', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 }
    ]
  },
  {
    name: 'HIIT Workout',
    description: 'High intensity interval training',
    difficulty: 'ADVANCED',
    durationMinutes: 8,
    imageRes: 'hiit_workout',
    exercises: [
      { name: 'Burpees', sets: 1, reps: 10, restSeconds: 10 },
      { name: 'Jumping Jacks', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'High Knees', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Burpees', sets: 1, reps: 10, restSeconds: 10 },
      { name: 'Jumping Jacks', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'High Knees', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Burpees', sets: 1, reps: 10, restSeconds: 10 },
      { name: 'Jumping Jacks', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'High Knees', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Burpees', sets: 1, reps: 10, restSeconds: 10 },
      { name: 'Jumping Jacks', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'High Knees', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 25, restSeconds: 10 }
    ]
  },
  {
    name: 'Arm Strength',
    description: 'Upper body arm builder',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 8,
    imageRes: 'armstrength_workout',
    exercises: [
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Tricep Dips', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Bicep Curls', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Tricep Dips', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Bicep Curls', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Tricep Dips', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Bicep Curls', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 }
    ]
  },
  {
    name: 'Back Workout',
    description: 'Back and posture focus',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 8,
    imageRes: 'back_workout',
    exercises: [
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 20, restSeconds: 10 }
    ]
  },
  {
    name: 'Back Builder',
    description: 'Build a stronger back',
    difficulty: 'ADVANCED',
    durationMinutes: 8,
    imageRes: 'backbuilder_workout',
    exercises: [
      { name: 'Push-ups', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 20, restSeconds: 10 }
    ]
  },
  {
    name: 'Cardio Blast',
    description: 'Get the heart pumping',
    difficulty: 'BEGINNER',
    durationMinutes: 8,
    imageRes: 'cardio_workout',
    exercises: [
      { name: 'Jumping Jacks', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'High Knees', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Jumping Jacks', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'High Knees', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Jumping Jacks', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'High Knees', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Jumping Jacks', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'High Knees', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 20, restSeconds: 10 }
    ]
  },
  {
    name: 'Chest Workout',
    description: 'Chest and push focus',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 8,
    imageRes: 'chest_workout',
    exercises: [
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Shoulder Taps', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 }
    ]
  },
  {
    name: 'Core Crush',
    description: 'Destroy your core',
    difficulty: 'ADVANCED',
    durationMinutes: 8,
    imageRes: 'corecrush_workout',
    exercises: [
      { name: 'Bicycle Crunches', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Russian Twists', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Bicycle Crunches', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Russian Twists', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Bicycle Crunches', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Russian Twists', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Bicycle Crunches', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Russian Twists', sets: 1, reps: 25, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 40, restSeconds: 10 },
      { name: 'Mountain Climbers', sets: 1, reps: 25, restSeconds: 10 }
    ]
  },
  {
    name: 'Full Body',
    description: 'Complete body workout',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 10,
    imageRes: 'fullbody_workout',
    exercises: [
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Lunges', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Burpees', sets: 1, reps: 10, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Push-ups', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Lunges', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Burpees', sets: 1, reps: 10, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Plank', sets: 1, reps: 30, restSeconds: 10 }
    ]
  },
  {
    name: 'Glute Power',
    description: 'Strong glutes, strong legs',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 8,
    imageRes: 'glutepower_workout',
    exercises: [
      { name: 'Glute Bridges', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Donkey Kicks', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Donkey Kicks', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Donkey Kicks', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 20, restSeconds: 10 },
      { name: 'Donkey Kicks', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 }
    ]
  },
  {
    name: 'Leg Day',
    description: 'Legs and lower body',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 8,
    imageRes: 'legday_workout',
    exercises: [
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Lunges', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Wall Sit', sets: 1, reps: 45, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Lunges', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Wall Sit', sets: 1, reps: 45, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Lunges', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Wall Sit', sets: 1, reps: 45, restSeconds: 10 },
      { name: 'Squats', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Lunges', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Wall Sit', sets: 1, reps: 45, restSeconds: 10 }
    ]
  },
  {
    name: 'Low Impact',
    description: 'Joint-friendly movement',
    difficulty: 'BEGINNER',
    durationMinutes: 8,
    imageRes: 'lowimpact_workout',
    exercises: [
      { name: 'Glute Bridges', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Donkey Kicks', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Wall Sit', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Donkey Kicks', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Wall Sit', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Donkey Kicks', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Wall Sit', sets: 1, reps: 30, restSeconds: 10 },
      { name: 'Glute Bridges', sets: 1, reps: 15, restSeconds: 10 },
      { name: 'Donkey Kicks', sets: 1, reps: 12, restSeconds: 10 },
      { name: 'Wall Sit', sets: 1, reps: 30, restSeconds: 10 }
    ]
  }
];

// ─────────────────────────────────────────────
// SEED FUNCTION
// ─────────────────────────────────────────────
const seedDB = async () => {
  try {
    await mongoose.connect(process.env.MONGODB_URI);
    console.log('✅ Connected to MongoDB');

    await Exercise.deleteMany({});
    await Workout.deleteMany({});
    await WorkoutExercise.deleteMany({});
    console.log('🗑️  Cleared existing data');

    const insertedExercises = await Exercise.insertMany(exercises);
    console.log(`✅ Inserted ${insertedExercises.length} exercises`);

    const exerciseMap = {};
    insertedExercises.forEach(ex => {
      exerciseMap[ex.name] = ex.exerciseId;
    });

    let totalJunctionEntries = 0;
    for (const workoutData of workouts) {
      // Calculate actual duration from this workout's exercises
      let totalSeconds = 0;
      workoutData.exercises.forEach(ex => {
        const exerciseData = insertedExercises.find(e => e.name === ex.name);
        const difficulty = exerciseData ? exerciseData.difficulty : 'BEGINNER';
        const workSeconds = difficulty === 'ADVANCED' ? 20 : difficulty === 'INTERMEDIATE' ? 30 : 45;
        totalSeconds += (ex.sets || 1) * workSeconds;
      });
      const calculatedDurationMinutes = Math.max(1, Math.ceil(totalSeconds / 60));

      const workout = new Workout({
        userId: 'system',
        name: workoutData.name,
        description: workoutData.description,
        difficulty: workoutData.difficulty,
        durationMinutes: calculatedDurationMinutes,
        isCustom: false
      });
      await workout.save();

      const junctionEntries = workoutData.exercises.map((ex, index) => ({
        workoutId: workout.workoutId,
        exerciseId: exerciseMap[ex.name],
        orderNumber: index + 1,
        sets: ex.sets,
        reps: ex.reps,
        restSeconds: ex.restSeconds
      }));

      await WorkoutExercise.insertMany(junctionEntries);
      totalJunctionEntries += junctionEntries.length;
    }

    console.log(`✅ Inserted ${workouts.length} workouts`);
    console.log(`✅ Inserted ${totalJunctionEntries} workout-exercise links`);
    console.log('🎉 Database seeded successfully!');

    process.exit();
  } catch (error) {
    console.error('❌ Error seeding database:', error);
    process.exit(1);
  }
};
seedDB();