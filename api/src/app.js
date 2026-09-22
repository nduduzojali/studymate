const express = require("express");
const cors = require("cors");

const authRoutes = require("./routes/auth");
const taskRoutes = require("./routes/tasks");
const userRoutes = require("./routes/users");

// The Express app is separated from server.js (which starts the server and
// connects to MongoDB) so that tests can import the app directly without
// opening a real network port.
function createApp() {
  const app = express();

  app.use(cors());
  app.use(express.json());

  // Simple request logger — satisfies the "make use of logging" requirement
  // and is genuinely useful for debugging the app during the demo video.
  app.use((req, res, next) => {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.originalUrl}`);
    next();
  });

  app.get("/", (req, res) => {
    res.json({ message: "StudyMate API is running." });
  });

  app.use("/api/auth", authRoutes);
  app.use("/api/tasks", taskRoutes);
  app.use("/api/users", userRoutes);

  // Fallback 404 handler
  app.use((req, res) => {
    res.status(404).json({ message: "Route not found." });
  });

  return app;
}

module.exports = createApp;
