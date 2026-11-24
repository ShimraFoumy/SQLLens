package com.sqlens.security;

import com.sqlens.exception.InvalidQueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlSecurityValidatorTest {

    private SqlSecurityValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SqlSecurityValidator();
    }

    @Test
    void shouldAcceptValidSelectQuery() {
        String query = "SELECT * FROM customers WHERE city = 'Colombo';";
        String cleaned = validator.validateAndSanitize(query);
        assertEquals("SELECT * FROM customers WHERE city = 'Colombo'", cleaned);
    }

    @Test
    void shouldAcceptValidCteQuery() {
        String query = "WITH top_orders AS (SELECT * FROM orders LIMIT 10) SELECT * FROM top_orders;";
        String cleaned = validator.validateAndSanitize(query);
        assertTrue(cleaned.startsWith("WITH"));
    }

    @Test
    void shouldRejectDropTable() {
        String query = "DROP TABLE customers;";
        InvalidQueryException ex = assertThrows(InvalidQueryException.class, () -> validator.validateAndSanitize(query));
        assertTrue(ex.getMessage().contains("Unauthorized statement type") || ex.getMessage().contains("Forbidden keyword"));
    }

    @Test
    void shouldRejectDeleteStatement() {
        String query = "DELETE FROM orders WHERE id = 1;";
        assertThrows(InvalidQueryException.class, () -> validator.validateAndSanitize(query));
    }

    @Test
    void shouldRejectUpdateStatement() {
        String query = "UPDATE employees SET salary = 100000 WHERE id = 5;";
        assertThrows(InvalidQueryException.class, () -> validator.validateAndSanitize(query));
    }

    @Test
    void shouldRejectMultiStatementInjection() {
        String query = "SELECT * FROM customers; DROP TABLE orders;";
        InvalidQueryException ex = assertThrows(InvalidQueryException.class, () -> validator.validateAndSanitize(query));
        assertTrue(ex.getMessage().contains("Multi-statement"));
    }

    @Test
    void shouldRejectEmptyQuery() {
        assertThrows(InvalidQueryException.class, () -> validator.validateAndSanitize("   "));
        assertThrows(InvalidQueryException.class, () -> validator.validateAndSanitize(null));
    }

    @Test
    void shouldStripCommentsSafely() {
        String query = "-- Fetch all users\nSELECT * FROM customers /* filter later */;";
        String cleaned = validator.validateAndSanitize(query);
        assertEquals("SELECT * FROM customers", cleaned);
    }
}
