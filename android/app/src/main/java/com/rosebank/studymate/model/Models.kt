package com.rosebank.studymate.model

// These data classes mirror the JSON shapes returned by the StudyMate REST
// API (see api/src/routes and the Part 1 Planning and Design document's
// data model). Gson uses the property names to map JSON fields directly.

data class User(
    val id: String? = null,
    val fullName: String,
    val email: String,
    val preferredLanguage: String? = "en",
    val xpPoints: Int? = 0,
    val streakCount: Int? = 0
)

data class Task(
    val _id: String? = null,
    val title: String,
    val module: String? = "",
    val dueDateTime: String? = null,
    val priority: String? = "Medium",
    val isComplete: Boolean = false
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateSettingsRequest(
    val fullName: String? = null,
    val preferredLanguage: String? = null,
    val newPassword: String? = null
)

data class ErrorResponse(
    val message: String
)
