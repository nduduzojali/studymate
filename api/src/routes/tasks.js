const express = require("express");
const Task = require("../models/Task");
const { verifyToken } = require("../middleware/auth");

const router = express.Router();
router.use(verifyToken); // every task route requires a logged-in user

// GET /api/tasks — list the authenticated user's tasks, newest due first.
router.get("/", async (req, res) => {
  try {
    const tasks = await Task.find({ owner: req.userId }).sort({ dueDateTime: 1 });
    return res.status(200).json(tasks);
  } catch (err) {
    console.error("List tasks error:", err.message);
    return res.status(500).json({ message: "Could not fetch tasks." });
  }
});

// POST /api/tasks — create a new task for the authenticated user.
router.post("/", async (req, res) => {
  try {
    const { title, module, dueDateTime, priority } = req.body;
    if (!title) {
      return res.status(400).json({ message: "Task title is required." });
    }
    const task = await Task.create({
      owner: req.userId,
      title,
      module,
      dueDateTime,
      priority,
    });
    return res.status(201).json(task);
  } catch (err) {
    console.error("Create task error:", err.message);
    return res.status(500).json({ message: "Could not create task." });
  }
});

// PUT /api/tasks/:id — update a task (e.g. toggle isComplete, edit fields).
router.put("/:id", async (req, res) => {
  try {
    const task = await Task.findOneAndUpdate(
      { _id: req.params.id, owner: req.userId },
      req.body,
      { new: true, runValidators: true }
    );
    if (!task) {
      return res.status(404).json({ message: "Task not found." });
    }
    return res.status(200).json(task);
  } catch (err) {
    console.error("Update task error:", err.message);
    return res.status(500).json({ message: "Could not update task." });
  }
});

// DELETE /api/tasks/:id
router.delete("/:id", async (req, res) => {
  try {
    const task = await Task.findOneAndDelete({ _id: req.params.id, owner: req.userId });
    if (!task) {
      return res.status(404).json({ message: "Task not found." });
    }
    return res.status(200).json({ message: "Task deleted." });
  } catch (err) {
    console.error("Delete task error:", err.message);
    return res.status(500).json({ message: "Could not delete task." });
  }
});

module.exports = router;
