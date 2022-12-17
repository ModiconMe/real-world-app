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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

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
     *
     * @param request dto for registration
     * @return dto
     */
    @Override
    public AccountDTO registry(AccountDTO.Registration request) {
        accountValidationService.registryValidation(request); // check that account registration request is valid, else throw
        AccountEntity account = mapper.mapToAccount(request);

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        return mapper.mapToAccountDTO(
                accountRepository.save(account)
        );
    }

    /**
     * Check that account exist by email, check for password
     *
     * @param request dto for auth
     * @return dto
     */
    @Override
    public AccountDTO auth(AccountDTO.Auth request) {
        AccountEntity account = accountValidationService.authValidation(request);
        String token = jwtUtils.generateToken(AccountDetails.builder()
                .id(account.getId())
                .email(account.getEmail())
                .password(account.getPassword())
                .build());
        return AccountDTO.builder()
                .email(account.getEmail())
                .username(account.getUsername())
                .bio(account.getBio())
                .image(account.getImage())
                .token(token)
                .build();
    }

    /**
     * Update account
     *
     * @param id      of updated account
     * @param request dto to update account
     * @return dto
     */
    @Override
    @Transactional
    public AccountDTO update(Long id, AccountDTO.Update request) {
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

        return mapper.mapToAccountDTO(account);
    }

    @Override
    public AccountDTO currentUser(Long userId) {
        Optional<AccountEntity> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isEmpty())
            throw new BadRequestException(format(ACCOUNT_NOT_FOUND_BY_ID, userId));
        return mapper.mapToAccountDTO(optionalAccount.get());
    }
}
