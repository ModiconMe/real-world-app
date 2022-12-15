package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.Account;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.utils.exception.BadRequestException;
import edu.popov.utils.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountValidationServiceImplTest {

    private AccountValidationService underTest;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String ACCOUNT_ALREADY_EXIST_BY_EMAIL = "Account with email %s already exist";
    private static final String ACCOUNT_ALREADY_EXIST_BY_USERNAME = "Account with username %s already exist";
    private static final String ACCOUNT_NOT_FOUND_BY_EMAIL = "Account with email %s is not exist";
    private static final String ACCOUNT_NOT_FOUND_BY_ID = "Account with id %d is not exist";
    private static final String WRONG_PASSWORD = "Invalid password for account with email %s";

    @BeforeEach
    void setUp() {
        underTest = new AccountValidationServiceImpl(accountRepository, passwordEncoder);
    }

    @Test
    void itShouldValidateAccountRegistry() {
        // given
        String email = "user1@gmail.com";
        String username = "user1";

        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .username(username)
                .email(email)
                .password("pass1")
                .build();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when
        Boolean expected = underTest.registryValidation(request);

        // then
        assertThat(expected).isNotNull();
        assertThat(expected).isTrue();
    }

    @Test
    void itShouldNotValidateAccountRegistry_whenEmailIsAlreadyExists() {
        // given
        String email = "user1@gmail.com";
        String username = "user1";
        Account account = Account.builder()
                .username(username)
                .email(email)
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .username(username)
                .email(email)
                .password("pass1")
                .build();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        // when
        // then
        assertThatThrownBy(() -> underTest.registryValidation(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ACCOUNT_ALREADY_EXIST_BY_EMAIL, email);
        then(accountRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldNotValidateAccountRegistry_whenUsernameIsAlreadyExists() {
        // given
        String email = "user1@gmail.com";
        String username = "user1";
        Account account = Account.builder()
                .username(username)
                .email(email)
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .username(username)
                .email(email)
                .password("pass1")
                .build();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));

        // when
        // then
        assertThatThrownBy(() -> underTest.registryValidation(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ACCOUNT_ALREADY_EXIST_BY_USERNAME, username);
        then(accountRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldValidateAccountAuth() {
        // given
        String email = "user1@gmail.com";
        String password = "pass1";
        Account account = Account.builder()
                .username("user1")
                .email(email)
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountDTO.Auth request = AccountDTO.Auth.builder()
                .email(email)
                .password(password)
                .build();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // when
        Account expected = underTest.authValidation(request);

        // then
        assertThat(expected).isEqualTo(account);
    }

    @Test
    void itShouldNotValidateAccountAuth_whenEmailIsNotExists() {
        // given
        String email = "user1@gmail.com";
        String password = "pass1";
        AccountDTO.Auth request = AccountDTO.Auth.builder()
                .email(email)
                .password(password)
                .build();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.authValidation(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ACCOUNT_NOT_FOUND_BY_EMAIL, email);
        then(accountRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldNotValidateAccountAuth_whenAccountAuthIsNotExists() {
        // given
        String email = "user1@gmail.com";
        String password = "pass1";
        Account account = Account.builder()
                .username("user1")
                .email(email)
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountDTO.Auth request = AccountDTO.Auth.builder()
                .email(email)
                .password(password)
                .build();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.authValidation(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(WRONG_PASSWORD, password);
        then(accountRepository).shouldHaveNoMoreInteractions();
    }

}