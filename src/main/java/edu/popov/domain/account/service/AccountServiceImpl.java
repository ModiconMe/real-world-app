package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.dto.AccountMapper;
import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.security.AccountDetails;
import edu.popov.security.jwt.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final AccountValidationService accountValidationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

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

        account.setUsername(request.getUsername());
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setBio(request.getBio());
        account.setImage(request.getImage());
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);

        return mapper.mapToAccountDTO(account);
    }

    @Override
    public UserDetails currentUser() {
        return (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
