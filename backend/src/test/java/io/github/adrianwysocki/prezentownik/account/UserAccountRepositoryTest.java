package io.github.adrianwysocki.prezentownik.account;
import io.github.adrianwysocki.prezentownik.person.Person;
import io.github.adrianwysocki.prezentownik.person.PersonRepository;
import jakarta.persistence.EntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
public class UserAccountRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    void shouldSaveUserAccount(){
        Person person = personRepository.saveAndFlush(new Person("Adam"));
        Role role = roleRepository.findByName("USER").orElseThrow();
        UserAccount userAccount = userAccountRepository.saveAndFlush(new UserAccount(person, "Adam", "Password", role));

        entityManager.clear();

        Optional<UserAccount> result = userAccountRepository.findById(person.getId());

        assertThat(result)
                .hasValueSatisfying(savedAccount -> {
                    assertThat(savedAccount.getPersonId()).isEqualTo(person.getId());
                    assertThat(savedAccount.getLogin()).isEqualTo("Adam");
                    assertThat(savedAccount.getPerson().getDisplayName()).isEqualTo("Adam");
                    assertThat(savedAccount.getRole().getName()).isEqualTo("USER");
                });
    }

    @Test
    void shouldFindByLogin(){
        Person person = personRepository.saveAndFlush(new Person("Adam"));
        Role role = roleRepository.findByName("USER").orElseThrow();
        UserAccount userAccount = userAccountRepository.saveAndFlush(new UserAccount(person, "Adam", "Password", role));

        entityManager.clear();

        Optional<UserAccount> result = userAccountRepository.findByLogin("Adam");

        assertThat(result)
                .hasValueSatisfying(savedAccount -> {
                    assertThat(savedAccount.getPersonId()).isEqualTo(person.getId());
                    assertThat(savedAccount.getLogin()).isEqualTo("Adam");
                    assertThat(savedAccount.getPerson().getDisplayName()).isEqualTo("Adam");
                    assertThat(savedAccount.getRole().getName()).isEqualTo("USER");
                });
    }

    @Test
    void shouldCheckLoginExists(){
        Person person = personRepository.saveAndFlush(new Person("Adam"));
        Role role = roleRepository.findByName("USER").orElseThrow();
        UserAccount userAccount = userAccountRepository.saveAndFlush(new UserAccount(person, "Adam", "Password", role));

        entityManager.clear();

        assertThat(userAccountRepository.existsByLogin("Adam")).isTrue();
        assertThat(userAccountRepository.existsByLogin("Bartek")).isFalse();
    }

    @Test
    void shouldRejectDuplicateLogin(){
        Person firstPerson = personRepository.saveAndFlush(new Person("Adam"));
        Person secondPerson = personRepository.saveAndFlush(new Person("Bartek"));
        Role role = roleRepository.findByName("USER").orElseThrow();

        userAccountRepository.saveAndFlush(new UserAccount(firstPerson, "Adam", "Password", role));

        assertThatThrownBy(() -> userAccountRepository.saveAndFlush(new UserAccount(secondPerson, "Adam", "Password", role)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectAccountWithoutRole(){
        Person person = personRepository.saveAndFlush(new Person("Adam"));

        assertThatThrownBy(() -> userAccountRepository.saveAndFlush(new UserAccount(person, "Adam", "Password", null)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectSecondAccountForSamePerson(){
        Person firstPerson = personRepository.saveAndFlush(new Person("Adam"));
        Role role = roleRepository.findByName("USER").orElseThrow();

        userAccountRepository.saveAndFlush(new UserAccount(firstPerson, "Adam", "Password", role));
        entityManager.clear();
        assertThatThrownBy(() -> userAccountRepository.saveAndFlush(new UserAccount(firstPerson, "Bartek", "Password", role)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
