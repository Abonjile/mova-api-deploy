const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const User = require('../models/User');

// POST /api/auth/register
router.post('/register', async (req, res) => {
    try {
        const { fullName, email, password, ssoProvider, ssoProviderId } = req.body;

        console.log('[auth/register] Received:', { fullName, email, hasPassword: !!password, ssoProvider });

        if (!email || !fullName) {
            return res.status(400).json({ message: 'fullName and email are required' });
        }

        // Require either password OR ssoProvider + ssoProviderId
        if (!password && (!ssoProvider || !ssoProviderId)) {
            return res.status(400).json({ message: 'Provide password OR ssoProvider + ssoProviderId' });
        }

        const existing = await User.findOne({ email });
        if (existing) {
            return res.status(400).json({ message: 'User already exists' });
        }

        const hashedPassword = password ? await bcrypt.hash(password, 10) : null;

        const userData = { fullName, email, password: hashedPassword };
        if (ssoProvider && ssoProviderId) {
            userData.ssoProvider = ssoProvider;
            userData.ssoProviderId = ssoProviderId;
        }

        const user = new User(userData);
        await user.save();

        console.log('[auth/register] Created user:', user.userId);

        res.status(201).json({
            message: 'User registered successfully',
            user: {
                userId: user.userId,
                fullName: user.fullName,
                email: user.email
            }
        });
    } catch (error) {
        console.error('[auth/register] Error:', error);
        res.status(500).json({ error: error.message });
    }
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
    try {
        const { email, password, ssoProvider, ssoProviderId } = req.body;

        console.log('[auth/login] Received:', { email, hasPassword: !!password, ssoProvider });

        if (!email) {
            return res.status(400).json({ message: 'Email is required' });
        }

        const user = await User.findOne({ email });
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        // --- Password login path ---
        if (password) {
            if (!user.password) {
                return res.status(401).json({
                    message: 'This account has no password. Use SSO or sign up again.'
                });
            }
            const valid = await bcrypt.compare(password, user.password);
            if (!valid) {
                return res.status(401).json({ message: 'Invalid credentials' });
            }
        }
        // --- SSO login path ---
        else if (ssoProvider && ssoProviderId) {
            if (user.ssoProvider !== ssoProvider || user.ssoProviderId !== ssoProviderId) {
                return res.status(401).json({ message: 'Invalid credentials' });
            }
        }
        // --- Neither path provided ---
        else {
            return res.status(400).json({ message: 'Provide password OR ssoProvider + ssoProviderId' });
        }

        res.json({
            message: 'Login successful',
            user: {
                userId: user.userId,
                fullName: user.fullName,
                email: user.email,
                weight: user.weight,
                height: user.height,
                fitnessLevel: user.fitnessLevel
            }
        });
    } catch (error) {
        console.error('[auth/login] Error:', error);
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;