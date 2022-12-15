package edu.popov.domain.account.repository;

import edu.popov.domain.account.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository underTest;

    @Test
    void itShouldNotRegisterAccount_whenEmailUniqueConstraintDisturbed() {
        // given
        String email = "user1@gmail.com";
        Account account1 = Account.builder()
                .username("user1")
                .email(email)
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        Account account2 = Account.builder()
                .username("user2")
                .email(email)
                .password("pass2")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        // when
        underTest.saveAndFlush(account1);

        // then
        assertThatThrownBy(() -> underTest.saveAndFlush(account2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotRegisterAccount_whenUsernameUniqueConstraintDisturbed() {
        // given
        String username = "user1";
        Account account1 = Account.builder()
                .username(username)
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        Account account2 = Account.builder()
                .username(username)
                .email("user2@gmail.com")
                .password("pass2")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        // when
        underTest.saveAndFlush(account1);

        // then
        assertThatThrownBy(() -> underTest.saveAndFlush(account2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldFindAccountByEmail() {
        // given
        String email = "user1@gmail.com";
        Account account = Account.builder()
                .username("user1")
                .email(email)
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        underTest.saveAndFlush(account);

        // when
        Optional<Account> optionalAccount = underTest.findByEmail(email);

        // then
        assertThat(optionalAccount).isPresent();
        assertThat(optionalAccount.get()).isEqualTo(account);
    }

    @Test
    void itShouldFindAccountByUsername() {
        // given
        String username = "user1";
        Account account = Account.builder()
                .username(username)
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        underTest.saveAndFlush(account);

        // when
        Optional<Account> optionalAccount = underTest.findByUsername(username);

        // then
        assertThat(optionalAccount).isPresent();
        assertThat(optionalAccount.get()).isEqualTo(account);
    }

    @Test
    void itShouldFindAccountByEmailAndPassword() {
        // given
        String email = "user1@gmail.com";
        String password = "pass1";
        Account account = Account.builder()
                .username("user1")
                .email(email)
                .password(password)
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        underTest.saveAndFlush(account);

        // when
        Optional<Account> optionalAccount = underTest.findByEmailAndPassword(email, password);

        // then
        assertThat(optionalAccount).isPresent();
        assertThat(optionalAccount.get()).isEqualTo(account);
    }
}