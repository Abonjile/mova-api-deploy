require('dotenv').config();
console.log('MONGODB_URI:', process.env.MONGODB_URI);

const express = require('express');
const cors = require('cors');
const connectDB = require('./config/database');

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
app.use('/api/users', require('./routes/userRoutes'));
app.use('/api/workouts', require('./routes/workoutRoutes'));
app.use('/api/challenges', require('./routes/challengeRoutes'));
app.use('/api/exercises', require('./routes/exerciseRoutes'));

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});