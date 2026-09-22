process.env.JWT_SECRET = "test-secret-key";

const mongoose = require("mongoose");
const request = require("supertest");
const { MongoMemoryServer } = require("mongodb-memory-server");
const createApp = require("../src/app");

let mongoServer;
let app;

beforeAll(async () => {
  mongoServer = await MongoMemoryServer.create();
  await mongoose.connect(mongoServer.getUri());
  app = createApp();
});

afterAll(async () => {
  await mongoose.disconnect();
  await mongoServer.stop();
});

describe("Auth endpoints", () => {
  test("registers a new user and never returns the plain password", async () => {
    const res = await request(app).post("/api/auth/register").send({
      fullName: "Ndu Jali",
      email: "ndu@example.com",
      password: "password123",
    });

    expect(res.status).toBe(201);
    expect(res.body.token).toBeDefined();
    expect(res.body.user.email).toBe("ndu@example.com");
    expect(res.body.user.passwordHash).toBeUndefined();
    expect(JSON.stringify(res.body)).not.toContain("password123");
  });

  test("rejects registration with a duplicate email", async () => {
    const res = await request(app).post("/api/auth/register").send({
      fullName: "Duplicate User",
      email: "ndu@example.com",
      password: "password123",
    });
    expect(res.status).toBe(409);
  });

  test("rejects registration with a short password", async () => {
    const res = await request(app).post("/api/auth/register").send({
      fullName: "Short Pass",
      email: "short@example.com",
      password: "123",
    });
    expect(res.status).toBe(400);
  });

  test("logs in with correct credentials", async () => {
    const res = await request(app).post("/api/auth/login").send({
      email: "ndu@example.com",
      password: "password123",
    });
    expect(res.status).toBe(200);
    expect(res.body.token).toBeDefined();
  });

  test("rejects login with wrong password", async () => {
    const res = await request(app).post("/api/auth/login").send({
      email: "ndu@example.com",
      password: "wrongpassword",
    });
    expect(res.status).toBe(401);
  });
});

describe("Task endpoints (require authentication)", () => {
  let token;

  beforeAll(async () => {
    const res = await request(app).post("/api/auth/register").send({
      fullName: "Task Tester",
      email: "tasks@example.com",
      password: "password123",
    });
    token = res.body.token;
  });

  test("rejects access without a token", async () => {
    const res = await request(app).get("/api/tasks");
    expect(res.status).toBe(401);
  });

  test("creates a task for the authenticated user", async () => {
    const res = await request(app)
      .post("/api/tasks")
      .set("Authorization", `Bearer ${token}`)
      .send({ title: "Submit OPSC POE", module: "OPSC6312", priority: "High" });

    expect(res.status).toBe(201);
    expect(res.body.title).toBe("Submit OPSC POE");
    expect(res.body.isComplete).toBe(false);
  });

  test("lists only the authenticated user's tasks", async () => {
    const res = await request(app).get("/api/tasks").set("Authorization", `Bearer ${token}`);
    expect(res.status).toBe(200);
    expect(res.body.length).toBe(1);
  });

  test("updates a task (mark complete)", async () => {
    const list = await request(app).get("/api/tasks").set("Authorization", `Bearer ${token}`);
    const taskId = list.body[0]._id;

    const res = await request(app)
      .put(`/api/tasks/${taskId}`)
      .set("Authorization", `Bearer ${token}`)
      .send({ isComplete: true });

    expect(res.status).toBe(200);
    expect(res.body.isComplete).toBe(true);
  });

  test("deletes a task", async () => {
    const list = await request(app).get("/api/tasks").set("Authorization", `Bearer ${token}`);
    const taskId = list.body[0]._id;

    const res = await request(app)
      .delete(`/api/tasks/${taskId}`)
      .set("Authorization", `Bearer ${token}`);

    expect(res.status).toBe(200);
  });
});
