package io.github.adrianwysocki.prezentownik.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class DatabaseMigrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldContainDefaultRolesAfterMigration() {
        List<String> roles = jdbcTemplate.queryForList("select name from role", String.class);
        assertThat(roles).contains("ADMIN", "USER");
    }
}