package io.github.adrianwysocki.prezentownik.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public class RoleRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private RoleRepository roleRepository;

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER"})
    void shouldFindRoleByName(String role){
        Optional<Role> roleOptional = roleRepository.findByName(role);
        assertThat(roleOptional)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r.getName()).isEqualTo(role));
    }

    @Test
    void shouldReturnEmptyForUnknownRole(){
        Optional<Role> roleOptional = roleRepository.findByName("MODERATOR");
        assertThat(roleOptional)
                .isEmpty();
    }

    @Test
    void shouldFindDefaultRoles(){
        List<Role> roles = roleRepository.findAll();
        assertThat(roles)
                .extracting(Role::getName)
                .contains("ADMIN", "USER");

    }
}
