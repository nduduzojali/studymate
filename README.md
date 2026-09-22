# StudyMate

**A South African student task, timetable and study-planner Android app.**
Built for OPSC6312 — Portfolio of Evidence, Part 2 (App Prototype Development).

**Author:** Nduduzo Jali (St10402988) — Rosebank College (The IIE)

---

## 1. Purpose of the App

StudyMate helps South African tertiary students manage assignments, class
timetables and study sessions in one place. It was designed after researching
three existing apps — My Study Life, Todoist and Google Tasks — none of which
fully serve South African students: dedicated planners feel dated and lack
local relevance, general productivity tools aren't built for academic
workflows, and lightweight tools are too limited for serious planning.
StudyMate combines the academic focus of a dedicated planner with motivating
gamification and genuine local relevance (multi-language support planned for
the final PoE).

Full research and design reasoning is documented in the Part 1 submission
(Research Report and Planning & Design Document).

## 2. Design Considerations

- **Architecture:** the Android app follows a simple, testable structure —
  Activities handle UI and user interaction, a `SessionManager` handles
  local auth-token storage, `RetrofitClient`/`ApiService` handle all network
  calls, and pure functions in `InputValidator` handle input validation so
  they can be unit tested independently of the Android framework.
- **Security:** passwords are never stored or transmitted in plain text.
  The API hashes every password with bcrypt before saving it, and all
  authenticated requests use a signed JWT bearer token.
- **Error handling:** every network call is wrapped so that a lost
  connection, an invalid input, or an unexpected server error shows the
  user a clear message instead of crashing the app.
- **Scope for Part 2:** per the module brief, PoE-only features (SSO,
  offline sync, real-time push notifications, multi-language support) were
  intentionally deferred to the final PoE submission. Part 2 focuses on a
  fully working, compiling prototype of the core flow: register, log in,
  manage tasks, and change account settings, backed by a REST API
  connected to a live, hosted database.

## 3. Project Structure

```
studymate-app/
├── android/          Android Studio project (Kotlin) — the StudyMate app
├── api/              Node.js/Express REST API + MongoDB database
└── .github/workflows/ GitHub Actions — automated testing for both parts
```

## 4. Tech Stack

| Layer | Technology |
|---|---|
| Mobile app | Kotlin, Android Views + ViewBinding, Retrofit, OkHttp, Kotlin Coroutines |
| Backend API | Node.js, Express, Mongoose |
| Database | MongoDB Atlas (cloud-hosted) |
| Auth | JWT (JSON Web Tokens) + bcrypt password hashing |
| Hosting | Render (API) |
| CI/CD | GitHub Actions (automated unit tests on every push) |

## 5. REST API Endpoints

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Log in, receive a JWT | No |
| GET | `/api/tasks` | List the logged-in user's tasks | Yes |
| POST | `/api/tasks` | Create a task | Yes |
| PUT | `/api/tasks/:id` | Update a task (e.g. mark complete) | Yes |
| DELETE | `/api/tasks/:id` | Delete a task | Yes |
| GET | `/api/users/me` | Get the logged-in user's profile | Yes |
| PUT | `/api/users/me` | Update name / password (Settings screen) | Yes |

The live, hosted API used by the app is at: `<INSERT YOUR RENDER URL HERE>`

## 6. Running the Project Locally

### API
```bash
cd api
npm install
cp .env.example .env   # then fill in your MongoDB Atlas URI and a JWT secret
npm run dev
```

### Android app
1. Open the `android/` folder in Android Studio.
2. Update `API_BASE_URL` in `app/build.gradle.kts` if you're pointing at a
   different deployed API URL (or `http://10.0.2.2:5000/` to hit your local
   API from the Android emulator).
3. Let Gradle sync, then Run on a device or emulator.

## 7. Automated Testing

This repository uses **GitHub Actions** to automatically build and test the
project on every push to `main`:

- `.github/workflows/android-ci.yml` runs the Android app's Kotlin/JUnit
  unit tests (`./gradlew test`) and builds a debug APK.
- `.github/workflows/api-ci.yml` runs the API's Jest/Supertest test suite
  covering registration, login, and task CRUD operations.

Both workflows can be seen running under this repository's **Actions** tab.

## 8. Version Control

This project was developed with regular, incremental commits pushed to
GitHub as features were completed (see commit history). The repository was
initialised with this README before feature work began.

## 9. Demonstration Video

📹 **Video link:** `<INSERT YOUR YOUTUBE (UNLISTED) LINK HERE>`

The video demonstrates:
- Registering a new account and logging in (with the encrypted password
  visible in the MongoDB Atlas dashboard, proving it is hashed, not plain text)
- Changing account settings
- Adding, completing and deleting a task, live against the hosted REST API
- The live data in MongoDB Atlas updating as the app is used
- A voice-over explaining each step

## 10. Use of AI Tools

See `AI_TOOLS_USE.md` for a full account of how AI tools were used during
this assessment, per the module's disclosure requirement.

## 11. References

- Express.js Documentation — https://expressjs.com/
- Mongoose Documentation — https://mongoosejs.com/docs/
- MongoDB Atlas Documentation — https://www.mongodb.com/docs/atlas/
- Retrofit Documentation — https://square.github.io/retrofit/
- Android Developers — App Architecture Guide — https://developer.android.com/topic/architecture
- JWT Introduction — https://jwt.io/introduction
