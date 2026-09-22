package com.rosebank.studymate

import com.rosebank.studymate.util.InputValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Local (JVM) unit tests — run with `./gradlew test` and by the GitHub
// Actions workflow on every push. These satisfy the module's "conduct
// detailed unit testing" requirement by testing the validation logic that
// every input screen (Login, Register, Add Task) relies on.
class InputValidatorTest {

    @Test
    fun `valid email addresses are accepted`() {
        assertTrue(InputValidator.isValidEmail("ndu@example.com"))
        assertTrue(InputValidator.isValidEmail("ndu.jali@rosebank.college.ac.za"))
    }

    @Test
    fun `invalid email addresses are rejected`() {
        assertFalse(InputValidator.isValidEmail(""))
        assertFalse(InputValidator.isValidEmail("not-an-email"))
        assertFalse(InputValidator.isValidEmail("missing@domain"))
        assertFalse(InputValidator.isValidEmail("@nodomain.com"))
    }

    @Test
    fun `passwords shorter than 6 characters are rejected`() {
        assertFalse(InputValidator.isValidPassword(""))
        assertFalse(InputValidator.isValidPassword("12345"))
    }

    @Test
    fun `passwords of 6 or more characters are accepted`() {
        assertTrue(InputValidator.isValidPassword("123456"))
        assertTrue(InputValidator.isValidPassword("password123"))
    }

    @Test
    fun `blank or whitespace-only text is treated as empty`() {
        assertFalse(InputValidator.isNonEmpty(""))
        assertFalse(InputValidator.isNonEmpty("   "))
        assertTrue(InputValidator.isNonEmpty("Submit OPSC POE"))
    }
}
