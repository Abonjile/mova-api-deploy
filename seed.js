require('dotenv').config();
const mongoose = require('mongoose');
const Exercise = require('./src/models/Exercise');

const exercises = [
  { name: 'Push-ups', description: 'Standard push-up', targetMuscleGroup: 'Chest', difficulty: 'BEGINNER', metValue: 3.8, videoUrl: '', defaultSets: 3, defaultReps: 12 },
  { name: 'Squats', description: 'Bodyweight squat', targetMuscleGroup: 'Legs', difficulty: 'BEGINNER', metValue: 5.0, videoUrl: '', defaultSets: 3, defaultReps: 15 },
  { name: 'Plank', description: 'Hold plank position', targetMuscleGroup: 'Core', difficulty: 'INTERMEDIATE', metValue: 3.0, videoUrl: '', defaultSets: 3, defaultReps: 30 },
  { name: 'Lunges', description: 'Forward lunge', targetMuscleGroup: 'Legs', difficulty: 'INTERMEDIATE', metValue: 4.0, videoUrl: '', defaultSets: 3, defaultReps: 10 },
  { name: 'Burpees', description: 'Full body explosive movement', targetMuscleGroup: 'Full Body', difficulty: 'ADVANCED', metValue: 8.0, videoUrl: '', defaultSets: 3, defaultReps: 10 },
  { name: 'Jumping Jacks', description: 'Cardio warm-up', targetMuscleGroup: 'Full Body', difficulty: 'BEGINNER', metValue: 7.0, videoUrl: '', defaultSets: 3, defaultReps: 20 }
];

const seedDB = async () => {
  try {
    await mongoose.connect(process.env.MONGODB_URI);
    await Exercise.deleteMany({});
    await Exercise.insertMany(exercises);
    console.log('✅ Seeded exercises!');
    process.exit();
  } catch (error) {
    console.error('❌ Error:', error);
    process.exit(1);
  }
};

seedDB();