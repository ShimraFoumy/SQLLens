package com.sqlens.security;

import com.sqlens.exception.InvalidQueryException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class SqlSecurityValidator {

    private static final Set<String> FORBIDDEN_KEYWORDS = new HashSet<>(Arrays.asList(
            "INSERT", "UPDATE", "DELETE", "DROP", "ALTER", "TRUNCATE",
            "CREATE", "GRANT", "REVOKE", "EXEC", "EXECUTE", "COPY",
            "VACUUM", "CALL", "DO", "LOCK", "REINDEX", "DISCARD", "SHUTDOWN"
    ));

    private static final Pattern COMMENT_PATTERN = Pattern.compile("(--[^\r\n]*)|(/\\*[\\s\\S]*?\\*/)");

    /**
     * Validates that the provided SQL statement is a safe, single SELECT or CTE query.
     *
     * @param rawSql the raw SQL string entered by the user
     * @return the cleaned, normalized SQL query
     * @throws InvalidQueryException if the query violates security constraints
     */
    public String validateAndSanitize(String rawSql) {
        if (rawSql == null || rawSql.trim().isEmpty()) {
            throw new InvalidQueryException("Query cannot be empty.");
        }

        // 1. Strip comments to prevent hidden injection
        String cleaned = COMMENT_PATTERN.matcher(rawSql).replaceAll(" ").trim();

        if (cleaned.isEmpty()) {
            throw new InvalidQueryException("Query contains only comments or whitespace.");
        }

        // 2. Prevent multi-statement execution (semicolon separation)
        // Remove trailing semicolon if present
        if (cleaned.endsWith(";")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1).trim();
        }

        if (cleaned.contains(";")) {
            throw new InvalidQueryException("Multi-statement queries are strictly disallowed. Please submit only one query at a time.");
        }

        // 3. Normalize whitespace for token inspection
        String normalized = cleaned.replaceAll("\\s+", " ").toUpperCase();

        // 4. Must start with SELECT, EXPLAIN, or WITH (Common Table Expression)
        if (!normalized.startsWith("SELECT") && !normalized.startsWith("WITH") && !normalized.startsWith("EXPLAIN")) {
            throw new InvalidQueryException("Unauthorized statement type. SQLLens only permits SELECT queries or WITH (CTE) expressions.");
        }

        // 5. Inspect individual word tokens to catch injected DDL/DML
        String[] tokens = normalized.split("[^A-Z0-9_]+");
        for (String token : tokens) {
            if (FORBIDDEN_KEYWORDS.contains(token)) {
                throw new InvalidQueryException("Forbidden keyword detected: '" + token + "'. Only read-only SELECT operations are permitted.");
            }
        }

        return cleaned;
    }
}
