package io.github.adrianwysocki.prezentownik.account;

import io.github.adrianwysocki.prezentownik.person.Person;
import jakarta.persistence.*;

@Entity
@Table(name = "user_account")
public class UserAccount {
    @Id
    private Long personId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(length = 100, nullable = false, unique = true, name = "login")
    private String login;

    @Column(length = 255, nullable = false, name = "password_hash")
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    protected UserAccount() {
    }

    public UserAccount(
            Person person,
            String login,
            String passwordHash,
            Role role
    ) {
        this.person = person;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getPersonId() {
        return personId;
    }

    public Person getPerson() {
        return person;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }
}
