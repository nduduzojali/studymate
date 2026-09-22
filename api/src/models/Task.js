const mongoose = require("mongoose");

// Task schema — matches the Task entity from the Part 1 data model.
const taskSchema = new mongoose.Schema(
  {
    owner: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true, index: true },
    title: { type: String, required: true, trim: true },
    module: { type: String, trim: true, default: "" },
    dueDateTime: { type: Date },
    priority: { type: String, enum: ["Low", "Medium", "High"], default: "Medium" },
    isComplete: { type: Boolean, default: false },
  },
  { timestamps: true }
);

module.exports = mongoose.model("Task", taskSchema);
