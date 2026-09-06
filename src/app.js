require('dotenv').config();
console.log('MONGODB_URI:', process.env.MONGODB_URI);
const connectDB = require('./config/database');

const express = require('express');
const cors = require('cors');

const app = express();

// Connect to database
connectDB();

// Middleware
app.use(cors());
app.use(express.json());

// Test route
app.get('/', (req, res) => {
  res.json({ message: 'Mova API is running!' });
});

// Routes
app.use('/api/auth', require('./routes/authRoutes'));
// app.use('/api/users', require('./routes/userRoutes'));
// app.use('/api/workouts', require('./routes/workoutRoutes'));
// app.use('/api/challenges', require('./routes/challengeRoutes'));

const PORT = process.env.PORT || 3000;
// TEST ROUTE — Create a test user
app.post('/test/user', async (req, res) => {
  try {
    const User = require('./models/User');
    const user = new User({
      fullName: 'Test User',
      email: 'test@mova.com',
      ssoProvider: 'GOOGLE',
      ssoProviderId: 'test123',
      weight: 70,
      height: 175,
      fitnessLevel: 'BEGINNER'
    });
    await user.save();
    res.json({ message: 'Test user created!', user });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});
app.listen(PORT, () => {
  console.log(`🚀 Server running on port ${PORT}`);
});