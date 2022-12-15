package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.dto.AccountMapper;
import edu.popov.domain.account.entity.Account;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.security.AccountDetails;
import edu.popov.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper mapper;

    @Mock
    private AccountValidationService accountValidationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    private AccountServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new AccountServiceImpl(accountRepository, mapper, accountValidationService, passwordEncoder, jwtUtils);
    }

    @Test
    void itShouldRegistryAccount() {
        // given
        Account account = Account.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountDTO accountDTO = AccountDTO.builder()
                .username("user1")
                .email("user1@gmail.com")
                .bio("bio")
                .image("image")
                .build();
        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        when(mapper.mapToAccount(request)).thenReturn(account);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(account.getPassword());
        when(accountRepository.save(account)).thenReturn(account);
        when(mapper.mapToAccountDTO(account)).thenReturn(accountDTO);

        // when
        underTest.registry(request);

        // then
        then(mapper).should().mapToAccount(request);
        then(passwordEncoder).should().encode(request.getPassword());
        then(mapper).should().mapToAccountDTO(account);
        then(accountRepository).should().save(account);
    }

    @Test
    void itShouldAuthAccount() {
        // given
        Account account = Account.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountDTO.Auth request = AccountDTO.Auth.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        AccountDetails accountDetails = AccountDetails.builder()
                .email("user1@gmail.com")
                .password("pass1")
                        .build();
        when(accountValidationService.authValidation(request)).thenReturn(account);
        when(jwtUtils.generateToken(accountDetails)).thenReturn(anyString());

        // when
        underTest.auth(request);

        // then
        then(accountValidationService).should().authValidation(request);
        then(jwtUtils).should().generateToken(accountDetails);
    }

    @Test
    void itShouldUpdateAccount() {
        // given
        Account account = Account.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountDTO accountDTO = AccountDTO.builder()
                .username("user2")
                .email("user1@gmail.com")
                .bio("bio")
                .image("image")
                .build();
        AccountDTO.Update request = AccountDTO.Update.builder()
                .username("user2")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .build();
        when(accountValidationService.updateValidation(1L, request)).thenReturn(account);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(account.getPassword());
        when(accountRepository.save(account)).thenReturn(account);
        when(mapper.mapToAccountDTO(account)).thenReturn(accountDTO);

        // when
        AccountDTO update = underTest.update(1L, request);

        // then
        assertThat(account.getUsername()).isEqualTo(request.getUsername());
        then(accountValidationService).should().updateValidation(1L, request);
        then(passwordEncoder).should().encode(request.getPassword());
        then(accountRepository).should().save(account);
        then(mapper).should().mapToAccountDTO(account);
    }

}