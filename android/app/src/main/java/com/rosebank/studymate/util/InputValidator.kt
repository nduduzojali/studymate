package com.rosebank.studymate.util

// Pure, framework-free validation functions used by Login/Register/AddTask
// screens to catch invalid input *before* it is sent to the API, so the
// app "handles invalid inputs without crashing" as the brief requires.
// Kept as standalone functions (rather than buried in an Activity), and
// deliberately avoiding android.util.Patterns (which is not available in
// plain JUnit tests without extra mocking), so they can be unit tested
// directly — see app/src/test/.../InputValidatorTest.kt
object InputValidator {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && EMAIL_REGEX.matches(email.trim())
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isNonEmpty(value: String): Boolean {
        return value.trim().isNotEmpty()
    }
}
