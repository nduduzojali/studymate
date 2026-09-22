require("dotenv").config();
const mongoose = require("mongoose");
const createApp = require("./app");

const PORT = process.env.PORT || 5000;
const MONGO_URI = process.env.MONGO_URI;

async function start() {
  try {
    await mongoose.connect(MONGO_URI);
    console.log("Connected to MongoDB Atlas.");

    const app = createApp();
    app.listen(PORT, () => {
      console.log(`StudyMate API listening on port ${PORT}`);
    });
  } catch (err) {
    console.error("Failed to start server:", err.message);
    process.exit(1);
  }
}

start();
