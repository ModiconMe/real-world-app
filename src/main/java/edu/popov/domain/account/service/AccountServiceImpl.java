package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.dto.AccountMapper;
import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.security.AccountDetails;
import edu.popov.security.jwt.JwtUtils;
import edu.popov.utils.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final AccountValidationService accountValidationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private static final String ACCOUNT_NOT_FOUND_BY_ID = "Account with id %d is not exist";

    /**
     * Check that account are not exist by email and username, register new account.
     */
    @Override
    public AccountDTO registry(AccountDTO.Registration request) {
        log.debug(request.toString());
        accountValidationService.registryValidation(request); // check that account registration request is valid, else throw
        AccountEntity account = mapper.mapToAccount(request);

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        AccountDTO accountDTO = mapper.mapToAccountDTO(
                accountRepository.save(account)
        );

        log.info(accountDTO.toString());
        return accountDTO;
    }

    /**
     * Login user and generate jwt token
     */
    @Override
    public AccountDTO auth(AccountDTO.Auth request) {
        log.debug(request.toString());
        AccountEntity account = accountValidationService.authValidation(request);
        String token = jwtUtils.generateToken(AccountDetails.builder()
                .id(account.getId())
                .email(account.getEmail())
                .password(account.getPassword())
                .build());


        AccountDTO accountDTO = AccountDTO.builder()
                .email(account.getEmail())
                .username(account.getUsername())
                .bio(account.getBio())
                .image(account.getImage())
                .token(token)
                .build();

        log.info(accountDTO.toString());
        return accountDTO;
    }

    /**
     * Update account
     */
    @Override
    @Transactional
    public AccountDTO update(Long id, AccountDTO.Update request) {
        log.debug("Update account with id {}, new account {}", id, request.toString());
        AccountEntity account = accountValidationService.updateValidation(id, request);

        if (Objects.nonNull(request.getUsername()))
            account.setUsername(request.getUsername());

        if (Objects.nonNull(request.getEmail()))
            account.setEmail(request.getEmail());

        if (Objects.nonNull(request.getPassword()))
            account.setPassword(passwordEncoder.encode(request.getPassword()));

        if (Objects.nonNull(request.getBio()))
            account.setBio(request.getBio());

        if (Objects.nonNull(request.getImage()))
            account.setImage(request.getImage());

        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);

        AccountDTO accountDTO = mapper.mapToAccountDTO(account);
        log.info(accountDTO.toString());
        return accountDTO;
    }

    /**
     * Return current user. Login required.
     */
    @Override
    public AccountDTO currentUser(Long userId) {
        Optional<AccountEntity> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isEmpty()) {
            String msg = format(ACCOUNT_NOT_FOUND_BY_ID, userId);
            log.error(msg);
            throw new BadRequestException(msg);
        }

        AccountDTO accountDTO = mapper.mapToAccountDTO(optionalAccount.get());
        log.info(accountDTO.toString());
        return accountDTO;
    }
}
