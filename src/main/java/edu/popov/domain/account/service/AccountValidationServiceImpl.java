package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.utils.exception.BadRequestException;
import edu.popov.utils.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class AccountValidationServiceImpl implements AccountValidationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ACCOUNT_ALREADY_EXIST_BY_EMAIL = "Account with email %s already exist";
    private static final String ACCOUNT_ALREADY_EXIST_BY_USERNAME = "Account with username %s already exist";
    private static final String ACCOUNT_NOT_FOUND_BY_EMAIL = "Account with email %s is not exist";
    private static final String ACCOUNT_NOT_FOUND_BY_ID = "Account with id %d is not exist";
    private static final String WRONG_PASSWORD = "Invalid password for account with email %s";

    /**
     * Validate account for unique email and username.
     */
    @Override
    public Boolean registryValidation(AccountDTO.Registration request) {

        String email = request.getEmail();
        if (accountRepository.findByEmail(email).isPresent())
            throw new BadRequestException(format(ACCOUNT_ALREADY_EXIST_BY_EMAIL, email));


        String username = request.getUsername();
        if (accountRepository.findByUsername(username).isPresent())
            throw new BadRequestException(format(ACCOUNT_ALREADY_EXIST_BY_USERNAME, username));

        return true;
    }

    /**
     * Validate account for existing email and password.
     */
    @Override
    public AccountEntity authValidation(AccountDTO.Auth request) {

        String email = request.getEmail();
        Optional<AccountEntity> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.isEmpty())
            throw new NotFoundException(format(ACCOUNT_NOT_FOUND_BY_EMAIL, email));

        AccountEntity account = optionalAccount.get();
        String encodedPass = account.getPassword();
        String rawPass = request.getPassword();

        if (!passwordEncoder.matches(rawPass, encodedPass))
            throw new NotFoundException(format(WRONG_PASSWORD, rawPass));

        return account;
    }

    /**
     * Validate account for existing by id and unique email and username.
     */
    @Override
    public AccountEntity updateValidation(Long id, AccountDTO.Update request) {
        Optional<AccountEntity> byId = accountRepository.findById(id);
        if (byId.isEmpty())
            throw new NotFoundException(format(ACCOUNT_NOT_FOUND_BY_ID, id));

        String email = request.getEmail();
        Optional<AccountEntity> byEmail = accountRepository.findByEmail(email);
        if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
            throw new BadRequestException(format(ACCOUNT_ALREADY_EXIST_BY_EMAIL, email));
        }

        String username = request.getUsername();
        Optional<AccountEntity> byUsername = accountRepository.findByUsername(username);
        if (byUsername.isPresent() && !byUsername.get().getId().equals(id)) {
            throw new BadRequestException(format(ACCOUNT_ALREADY_EXIST_BY_USERNAME, username));
        }

        return byId.get();
    }
}
