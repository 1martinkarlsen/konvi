package com.konvi.config

import kotlin.test.Test
import kotlin.test.assertFailsWith

private val secureConfig = Config(
    database = DatabaseConfig(url = "jdbc:postgresql://db.internal:5432/app"),
    auth = AuthConfig(jwt = JwtAuthConfig(secret = "a-real-secret"))
)

private val insecureConfig = Config()

class ConfigValidationTest {

    @Test
    fun `validateConfig throws in PRODUCTION when jwt secret is the insecure default`() {
        val config = secureConfig.copy(auth = AuthConfig(jwt = JwtAuthConfig(secret = "change-me")))

        val exception = assertFailsWith<IllegalStateException> {
            validateConfig(config, Environment.PRODUCTION)
        }
        assert(exception.message!!.contains("KONVI_AUTH_JWT_SECRET"))
    }

    @Test
    fun `validateConfig throws in PRODUCTION when database url is in-memory H2`() {
        val config = secureConfig.copy(database = DatabaseConfig(url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"))

        val exception = assertFailsWith<IllegalStateException> {
            validateConfig(config, Environment.PRODUCTION)
        }
        assert(exception.message!!.contains("KONVI_DATABASE_URL"))
    }

    @Test
    fun `validateConfig throws in PRODUCTION with all violations listed`() {
        val exception = assertFailsWith<IllegalStateException> {
            validateConfig(insecureConfig, Environment.PRODUCTION)
        }
        assert(exception.message!!.contains("KONVI_AUTH_JWT_SECRET"))
        assert(exception.message!!.contains("KONVI_DATABASE_URL"))
    }

    @Test
    fun `validateConfig does not throw in PRODUCTION when config is secure`() {
        validateConfig(secureConfig, Environment.PRODUCTION)
    }

    @Test
    fun `validateConfig does not throw in DEVELOPMENT even with insecure defaults`() {
        validateConfig(insecureConfig, Environment.DEVELOPMENT)
    }
}
