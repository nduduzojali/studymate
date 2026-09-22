const mongoose = require("mongoose");

// User schema — matches the data model defined in the StudyMate Part 1
// Planning and Design document (User entity).
const userSchema = new mongoose.Schema(
  {
    fullName: { type: String, required: true, trim: true },
    email: { type: String, required: true, unique: true, lowercase: true, trim: true },
    // Never store the plain-text password — only the bcrypt hash.
    passwordHash: { type: String, required: true },
    preferredLanguage: { type: String, enum: ["en", "zu", "tn"], default: "en" },
    xpPoints: { type: Number, default: 0 },
    streakCount: { type: Number, default: 0 },
  },
  { timestamps: true }
);

module.exports = mongoose.model("User", userSchema);
