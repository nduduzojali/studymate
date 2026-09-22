const express = require("express");
const bcrypt = require("bcryptjs");
const User = require("../models/User");
const { verifyToken } = require("../middleware/auth");

const router = express.Router();
router.use(verifyToken);

// GET /api/users/me — used by the Settings screen to pre-fill current details.
router.get("/me", async (req, res) => {
  try {
    const user = await User.findById(req.userId).select("-passwordHash");
    if (!user) return res.status(404).json({ message: "User not found." });
    return res.status(200).json(user);
  } catch (err) {
    console.error("Get profile error:", err.message);
    return res.status(500).json({ message: "Could not fetch profile." });
  }
});

// PUT /api/users/me — the "change their settings" requirement: lets the
// user update their name, preferred language and/or password.
router.put("/me", async (req, res) => {
  try {
    const { fullName, preferredLanguage, newPassword } = req.body;
    const update = {};
    if (fullName) update.fullName = fullName;
    if (preferredLanguage) update.preferredLanguage = preferredLanguage;
    if (newPassword) {
      if (newPassword.length < 6) {
        return res.status(400).json({ message: "New password must be at least 6 characters." });
      }
      update.passwordHash = await bcrypt.hash(newPassword, 10);
    }

    const user = await User.findByIdAndUpdate(req.userId, update, {
      new: true,
      runValidators: true,
    }).select("-passwordHash");

    return res.status(200).json(user);
  } catch (err) {
    console.error("Update profile error:", err.message);
    return res.status(500).json({ message: "Could not update profile." });
  }
});

module.exports = router;
