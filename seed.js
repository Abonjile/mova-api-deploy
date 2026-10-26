require('dotenv').config();
const mongoose = require('mongoose');
const Exercise = require('./src/models/Exercise');
const Workout = require('./src/models/Workout');
const WorkoutExercise = require('./src/models/WorkoutExercise');

// ─────────────────────────────────────────────
// 16 EXERCISES
// ─────────────────────────────────────────────
const exercises = [
  { name: 'Push-ups', description: 'Standard push-up', targetMuscleGroup: 'Chest', difficulty: 'BEGINNER', metValue: 3.8, defaultSets: 3, defaultReps: 12, videoUrl: '' },
  { name: 'Squats', description: 'Bodyweight squat', targetMuscleGroup: 'Legs', difficulty: 'BEGINNER', metValue: 5.0, defaultSets: 3, defaultReps: 15, videoUrl: '' },
  { name: 'Plank', description: 'Hold plank position', targetMuscleGroup: 'Core', difficulty: 'INTERMEDIATE', metValue: 3.0, defaultSets: 3, defaultReps: 30, videoUrl: '' },
  { name: 'Lunges', description: 'Forward lunge', targetMuscleGroup: 'Legs', difficulty: 'INTERMEDIATE', metValue: 4.0, defaultSets: 3, defaultReps: 10, videoUrl: '' },
  { name: 'Burpees', description: 'Full body explosive movement', targetMuscleGroup: 'Full Body', difficulty: 'ADVANCED', metValue: 8.0, defaultSets: 3, defaultReps: 10, videoUrl: '' },
  { name: 'Jumping Jacks', description: 'Cardio warm-up', targetMuscleGroup: 'Full Body', difficulty: 'BEGINNER', metValue: 7.0, defaultSets: 3, defaultReps: 20, videoUrl: '' },
  { name: 'Mountain Climbers', description: 'Dynamic core exercise', targetMuscleGroup: 'Core', difficulty: 'INTERMEDIATE', metValue: 8.0, defaultSets: 3, defaultReps: 20, videoUrl: '' },
  { name: 'Glute Bridges', description: 'Hip raise for glutes', targetMuscleGroup: 'Glutes', difficulty: 'BEGINNER', metValue: 3.5, defaultSets: 3, defaultReps: 15, videoUrl: '' },
  { name: 'Donkey Kicks', description: 'Quadruped hip extension', targetMuscleGroup: 'Glutes', difficulty: 'BEGINNER', metValue: 3.5, defaultSets: 3, defaultReps: 12, videoUrl: '' },
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
    durationMinutes: 20,
    imageRes: 'abs_workout',
    exercises: [
      { name: 'Bicycle Crunches', sets: 3, reps: 20, restSeconds: 30 },
      { name: 'Russian Twists', sets: 3, reps: 20, restSeconds: 30 },
      { name: 'Plank', sets: 3, reps: 45, restSeconds: 30 },
      { name: 'Mountain Climbers', sets: 3, reps: 20, restSeconds: 30 }
    ]
  },
  {
    name: 'HIIT Workout',
    description: 'High intensity interval training',
    difficulty: 'ADVANCED',
    durationMinutes: 25,
    imageRes: 'hiit_workout',
    exercises: [
      { name: 'Burpees', sets: 4, reps: 10, restSeconds: 20 },
      { name: 'Jumping Jacks', sets: 4, reps: 30, restSeconds: 20 },
      { name: 'High Knees', sets: 4, reps: 40, restSeconds: 20 },
      { name: 'Mountain Climbers', sets: 4, reps: 25, restSeconds: 20 }
    ]
  },
  {
    name: 'Arm Strength',
    description: 'Upper body arm builder',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 30,
    imageRes: 'armstrength_workout',
    exercises: [
      { name: 'Push-ups', sets: 4, reps: 12, restSeconds: 45 },
      { name: 'Tricep Dips', sets: 3, reps: 12, restSeconds: 45 },
      { name: 'Bicep Curls', sets: 3, reps: 12, restSeconds: 45 },
      { name: 'Plank', sets: 3, reps: 45, restSeconds: 45 }
    ]
  },
  {
    name: 'Back Workout',
    description: 'Back and posture focus',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 30,
    imageRes: 'back_workout',
    exercises: [
      { name: 'Plank', sets: 3, reps: 45, restSeconds: 45 },
      { name: 'Glute Bridges', sets: 3, reps: 15, restSeconds: 30 },
      { name: 'Mountain Climbers', sets: 3, reps: 20, restSeconds: 30 },
      { name: 'Shoulder Taps', sets: 3, reps: 20, restSeconds: 30 }
    ]
  },
  {
    name: 'Back Builder',
    description: 'Build a stronger back',
    difficulty: 'ADVANCED',
    durationMinutes: 35,
    imageRes: 'backbuilder_workout',
    exercises: [
      { name: 'Push-ups', sets: 4, reps: 15, restSeconds: 45 },
      { name: 'Plank', sets: 4, reps: 60, restSeconds: 45 },
      { name: 'Shoulder Taps', sets: 4, reps: 25, restSeconds: 45 },
      { name: 'Glute Bridges', sets: 4, reps: 20, restSeconds: 45 }
    ]
  },
  {
    name: 'Cardio Blast',
    description: 'Get the heart pumping',
    difficulty: 'BEGINNER',
    durationMinutes: 20,
    imageRes: 'cardio_workout',
    exercises: [
      { name: 'Jumping Jacks', sets: 3, reps: 30, restSeconds: 20 },
      { name: 'High Knees', sets: 3, reps: 40, restSeconds: 20 },
      { name: 'Mountain Climbers', sets: 3, reps: 20, restSeconds: 20 }
    ]
  },
  {
    name: 'Chest Workout',
    description: 'Chest and push focus',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 25,
    imageRes: 'chest_workout',
    exercises: [
      { name: 'Push-ups', sets: 4, reps: 12, restSeconds: 45 },
      { name: 'Shoulder Taps', sets: 3, reps: 20, restSeconds: 30 },
      { name: 'Plank', sets: 3, reps: 45, restSeconds: 30 }
    ]
  },
  {
    name: 'Core Crush',
    description: 'Destroy your core',
    difficulty: 'ADVANCED',
    durationMinutes: 30,
    imageRes: 'corecrush_workout',
    exercises: [
      { name: 'Bicycle Crunches', sets: 4, reps: 25, restSeconds: 30 },
      { name: 'Russian Twists', sets: 4, reps: 25, restSeconds: 30 },
      { name: 'Plank', sets: 4, reps: 60, restSeconds: 30 },
      { name: 'Mountain Climbers', sets: 4, reps: 25, restSeconds: 30 }
    ]
  },
  {
    name: 'Full Body',
    description: 'Complete body workout',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 35,
    imageRes: 'fullbody_workout',
    exercises: [
      { name: 'Push-ups', sets: 3, reps: 12, restSeconds: 30 },
      { name: 'Squats', sets: 3, reps: 15, restSeconds: 30 },
      { name: 'Plank', sets: 3, reps: 45, restSeconds: 30 },
      { name: 'Lunges', sets: 3, reps: 12, restSeconds: 30 },
      { name: 'Burpees', sets: 3, reps: 10, restSeconds: 30 }
    ]
  },
  {
    name: 'Glute Power',
    description: 'Strong glutes, strong legs',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 25,
    imageRes: 'glutepower_workout',
    exercises: [
      { name: 'Glute Bridges', sets: 4, reps: 20, restSeconds: 30 },
      { name: 'Donkey Kicks', sets: 4, reps: 15, restSeconds: 30 },
      { name: 'Squats', sets: 3, reps: 15, restSeconds: 30 }
    ]
  },
  {
    name: 'Leg Day',
    description: 'Legs and lower body',
    difficulty: 'INTERMEDIATE',
    durationMinutes: 30,
    imageRes: 'legday_workout',
    exercises: [
      { name: 'Squats', sets: 4, reps: 15, restSeconds: 45 },
      { name: 'Lunges', sets: 4, reps: 12, restSeconds: 45 },
      { name: 'Wall Sit', sets: 3, reps: 60, restSeconds: 45 }
    ]
  },
  {
    name: 'Low Impact',
    description: 'Joint-friendly movement',
    difficulty: 'BEGINNER',
    durationMinutes: 20,
    imageRes: 'lowimpact_workout',
    exercises: [
      { name: 'Glute Bridges', sets: 3, reps: 15, restSeconds: 30 },
      { name: 'Donkey Kicks', sets: 3, reps: 12, restSeconds: 30 },
      { name: 'Wall Sit', sets: 3, reps: 30, restSeconds: 30 }
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

    // Clear existing data
    await Exercise.deleteMany({});
    await Workout.deleteMany({});
    await WorkoutExercise.deleteMany({});
    console.log('🗑️  Cleared existing data');

    // Insert exercises
    const insertedExercises = await Exercise.insertMany(exercises);
    console.log(`✅ Inserted ${insertedExercises.length} exercises`);

    // Build a name → exerciseId map
    const exerciseMap = {};
    insertedExercises.forEach(ex => {
      exerciseMap[ex.name] = ex.exerciseId;
    });

    // Insert workouts and their junction entries
    let totalJunctionEntries = 0;
    for (const workoutData of workouts) {
      const workout = new Workout({
        userId: 'system',
        name: workoutData.name,
        description: workoutData.description,
        difficulty: workoutData.difficulty,
        durationMinutes: workoutData.durationMinutes,
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