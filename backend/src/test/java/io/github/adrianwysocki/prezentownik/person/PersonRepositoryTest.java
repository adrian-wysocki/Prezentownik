package io.github.adrianwysocki.prezentownik.person;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
public class PersonRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSavePerson() {
        // Arrange
        Person person = new Person("Test");

        // Act
        Person savedPerson = personRepository.saveAndFlush(person);

        // Assert
        assertThat(savedPerson.getId()).isNotNull();
        assertThat(savedPerson.getDisplayName()).isEqualTo("Test");
    }

    @Test
    void shouldFindPersonById(){
        // Arrange
        Person person = new Person("Test");

        // Act
        Person savedPerson = personRepository.saveAndFlush(person);
        entityManager.clear();
        Optional<Person> foundPerson = personRepository.findById(savedPerson.getId());

        // Assert
        assertThat(foundPerson)
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p.getDisplayName()).isEqualTo("Test");
                    assertThat(p.getId()).isEqualTo(savedPerson.getId());
                });
    }

    @Test
    void shouldRejectDuplicateDisplayNames(){
        Person firstPerson = new Person("Test");
        personRepository.saveAndFlush(firstPerson);
        Person secondPerson = new Person("Test");

        assertThatThrownBy(() -> personRepository.saveAndFlush(secondPerson))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void shouldRejectBlankDisplayName(String displayName){
        Person person = new Person(displayName);

        assertThatThrownBy(() -> personRepository.saveAndFlush(person))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectNullDisplayName(){
        Person person = new Person(null);

        assertThatThrownBy(() -> personRepository.saveAndFlush(person))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectTooLongDisplayName(){
        Person person = new Person("a".repeat(101));

        assertThatThrownBy(() -> personRepository.saveAndFlush(person))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
