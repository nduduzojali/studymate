# Use of AI Tools — Part 2 Declaration

For this assessment I used Claude (an AI assistant by Anthropic) as a
coding, debugging and documentation aid while building the StudyMate
prototype. Below is an honest account of how it was used and cited, as
required by the module brief.

**Code generation:** I directed the overall architecture and feature scope
for Part 2 (register/login with encrypted passwords, settings, a REST API
connected to a hosted database, and task management), scoped specifically
to exclude the PoE-only features per the brief. Claude wrote the initial
Kotlin source for the Android app (Activities, layouts, the Retrofit
network layer, the session manager) and the Node.js/Express REST API
(routes, MongoDB models, JWT authentication middleware, password hashing)
based on that direction and on the data model and API design I specified
in my own Part 1 Planning and Design Document.

**Debugging and verification:** Claude ran the API's logic directly (not
just generated it) — booting the real Express server and sending it live
HTTP requests to confirm registration, login, validation, and error
handling worked correctly, and verifying the bcrypt password-hashing and
JWT token logic in isolation. It could not compile or run the Android/
Kotlin code itself (no Android SDK available in its environment), so I
take responsibility for compiling, running, and fixing any build errors in
Android Studio myself.

**Automated testing:** Claude wrote the Jest/Supertest test suite for the
API (covering registration, login, and task CRUD) and the JUnit unit tests
for the Android input-validation logic, and the GitHub Actions workflow
files that run these tests automatically on every push.

**Documentation:** Claude drafted this README and the AI-tools write-up
based on the actual code and decisions in the repository.

**What remains my own work:** the overall app concept, architecture
decisions, and feature scope; setting up and configuring the MongoDB
Atlas database and Render hosting; initialising the Git repository and
managing all commits/pushes; compiling and running the app in Android
Studio and fixing any environment-specific build issues; and recording
the demonstration video myself. I take full responsibility for reviewing
and understanding all AI-assisted code before submission.
