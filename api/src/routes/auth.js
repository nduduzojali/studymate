const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const User = require("../models/User");

const router = express.Router();

function signToken(userId) {
  return jwt.sign({ userId }, process.env.JWT_SECRET, { expiresIn: "30d" });
}

function publicUser(user) {
  return {
    id: user._id,
    fullName: user.fullName,
    email: user.email,
    preferredLanguage: user.preferredLanguage,
    xpPoints: user.xpPoints,
    streakCount: user.streakCount,
  };
}

// POST /api/auth/register
// Registers a new user. The password is hashed with bcrypt before saving —
// the plain-text password is never stored, satisfying the module's
// "encrypt the password" requirement.
router.post("/register", async (req, res) => {
  try {
    const { fullName, email, password } = req.body;

    if (!fullName || !email || !password) {
      return res.status(400).json({ message: "fullName, email and password are all required." });
    }
    if (password.length < 6) {
      return res.status(400).json({ message: "Password must be at least 6 characters long." });
    }

    const existing = await User.findOne({ email: email.toLowerCase() });
    if (existing) {
      return res.status(409).json({ message: "An account with this email already exists." });
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const user = await User.create({ fullName, email, passwordHash });

    const token = signToken(user._id);
    return res.status(201).json({ token, user: publicUser(user) });
  } catch (err) {
    console.error("Register error:", err.message);
    return res.status(500).json({ message: "Something went wrong while registering." });
  }
});

// POST /api/auth/login
router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) {
      return res.status(400).json({ message: "Email and password are required." });
    }

    const user = await User.findOne({ email: email.toLowerCase() });
    if (!user) {
      return res.status(401).json({ message: "Invalid email or password." });
    }

    const passwordMatches = await bcrypt.compare(password, user.passwordHash);
    if (!passwordMatches) {
      return res.status(401).json({ message: "Invalid email or password." });
    }

    const token = signToken(user._id);
    return res.status(200).json({ token, user: publicUser(user) });
  } catch (err) {
    console.error("Login error:", err.message);
    return res.status(500).json({ message: "Something went wrong while logging in." });
  }
});

module.exports = router;
