const User = require('../models/User'); // Adjust path to User model if needed
const Reward = require('../models/Reward');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// Register User & Auto-Seed Default Rewards
exports.register = async (req, res) => {
    try {
        const { username, email, password } = req.body;

        // Check if user already exists
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ success: false, message: "User already exists" });
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);
        const newUser = new User({ username, email, password: hashedPassword });
        await newUser.save();

        // Create default reward entry so new users immediately have claimable items
        const defaultReward = new Reward({
            userId: newUser._id.toString(),
            streakDays: 1,
            consistencyLevel: '14% weekly',
            updatedPoints: 50,
            claimableRewards: [
                {
                    id: 'reward_first_step',
                    name: 'First Step Badge',
                    description: 'Unlocked immediately upon completing your first introductory goal.',
                    isUnlocked: true,
                    isClaimed: false,
                    iconUrl: 'ic_number_one_trophy'
                }
            ],
            milestoneBadges: [
                {
                    id: 'badge_champion',
                    name: 'Champion',
                    description: 'Win a challenge',
                    isUnlocked: true,
                    isClaimed: true,
                    iconUrl: 'ic_golden_trophy'
                },
                {
                    id: 'badge_4day',
                    name: '4-Day Challenge Trophy',
                    description: 'Complete a 4-Day Challenge',
                    isUnlocked: true,
                    isClaimed: false,
                    iconUrl: 'ic_golden_trophy'
                },
                {
                    id: 'badge_7day',
                    name: '7-Day Challenge Trophy',
                    description: 'Complete a 7-Day Challenge',
                    isUnlocked: false,
                    isClaimed: false,
                    iconUrl: 'ic_golden_trophy'
                }
            ]
        });

        await defaultReward.save();

        return res.status(201).json({
            success: true,
            message: "User registered successfully",
            userId: newUser._id
        });

    } catch (error) {
        console.error("Register error:", error);
        return res.status(500).json({ success: false, message: error.message });
    }
};

// Login User
exports.login = async (req, res) => {
    try {
        const { email, password } = req.body;
        const user = await User.findOne({ email });
        if (!user) {
            return res.status(400).json({ success: false, message: "Invalid credentials" });
        }

        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ success: false, message: "Invalid credentials" });
        }

        const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET || 'secret_key', { expiresIn: '1d' });

        return res.status(200).json({
            success: true,
            message: "Login successful",
            token,
            userId: user._id
        });

    } catch (error) {
        console.error("Login error:", error);
        return res.status(500).json({ success: false, message: error.message });
    }
};