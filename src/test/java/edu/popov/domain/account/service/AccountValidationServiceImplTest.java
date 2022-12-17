package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.utils.exception.BadRequestException;
import edu.popov.utils.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.MethodSource;
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
    @MethodSource
    void itShouldNotValidateAccountRegistry_whenEmailIsAlreadyExists() {
        // given
        String email = "user1@gmail.com";
        String username = "user1";
        AccountEntity account = AccountEntity.builder()
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
        AccountEntity account = AccountEntity.builder()
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
        AccountEntity account = AccountEntity.builder()
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
        AccountEntity expected = underTest.authValidation(request);

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
        AccountEntity account = AccountEntity.builder()
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

    @Test
    void itShouldValidateAccountUpdate() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountDTO.Update request = AccountDTO.Update.builder()
                .email("user1@gmail.com")
                .username("user2")
                .password("pass1")
                .bio("bio")
                .image("image")
                .build();

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(accountRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // when
        AccountEntity expected = underTest.updateValidation(1L, request);

        // then
        assertThat(expected).isEqualTo(account);
    }

    @Test
    void itShouldNotValidateAccountUpdate_whenIdIsNotExists() {
        // given
        AccountEntity account1 = AccountEntity.builder()
                .id(1L)
                .build();
        AccountDTO.Update request = AccountDTO.Update.builder()
                .email("user1@gmail.com")
                .username("user2")
                .password("pass1")
                .bio("bio")
                .image("image")
                .build();

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateValidation(account1.getId(), request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ACCOUNT_NOT_FOUND_BY_ID, account1.getId());
    }

    @Test
    void itShouldNotValidateAccountUpdate_whenEmailIsAlreadyExists() {
        // given
        AccountEntity account1 = AccountEntity.builder()
                .id(1L)
                .build();
        AccountEntity account2 = AccountEntity.builder()
                .id(2L)
                .email("user1@gmail.com")
                .build();
        AccountDTO.Update request = AccountDTO.Update.builder()
                .email("user1@gmail.com")
                .username("user2")
                .password("pass1")
                .bio("bio")
                .image("image")
                .build();

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(account2));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateValidation(account1.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ACCOUNT_ALREADY_EXIST_BY_EMAIL, request.getEmail());
    }

    @Test
    void itShouldNotValidateAccountUpdate_whenUsernameIsAlreadyExists() {
        // given
        AccountEntity account1 = AccountEntity.builder()
                .id(1L)
                .build();
        AccountEntity account2 = AccountEntity.builder()
                .id(2L)
                .username("user1")
                .build();
        AccountDTO.Update request = AccountDTO.Update.builder()
                .email("user1@gmail.com")
                .username("user2")
                .password("pass1")
                .bio("bio")
                .image("image")
                .build();

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(accountRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(account2));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateValidation(account1.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ACCOUNT_ALREADY_EXIST_BY_USERNAME, request.getUsername());
    }
}