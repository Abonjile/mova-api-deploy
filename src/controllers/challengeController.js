const Challenge = require('../models/Challenge');
const UserChallenge = require('../models/UserChallenge');

// @desc    Get all active challenges
// @route   GET /api/challenges
exports.getAllChallenges = async (req, res) => {
  try {
    const challenges = await Challenge.find({ isActive: true });
    res.status(200).json({ success: true, data: challenges });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Join a challenge for a user
// @route   POST /api/challenges/join
exports.joinChallenge = async (req, res) => {
  try {
    const { userId, challengeId } = req.body;
    const userChallenge = new UserChallenge({
      userId,
      challengeId
    });
    const savedRecord = await userChallenge.save();
    res.status(201).json({ success: true, data: savedRecord });
  } catch (error) {
    res.status(400).json({ success: false, message: error.message });
  }
};

// @desc    Update challenge progress, streaks, and custom reflection notes
// @route   PUT /api/challenges/:userChallengeId
exports.updateChallengeProgress = async (req, res) => {
  try {
    const { userChallengeId } = req.params;
    const { currentDay, streakCount, completed, customNote, badgeEarned } = req.body;

    const updated = await UserChallenge.findOneAndUpdate(
      { userChallengeId },
      { currentDay, streakCount, completed, customNote, badgeEarned },
      { new: true }
    );

    if (!updated) {
      return res.status(404).json({ success: false, message: 'User challenge record not found' });
    }

    res.status(200).json({ success: true, data: updated });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};