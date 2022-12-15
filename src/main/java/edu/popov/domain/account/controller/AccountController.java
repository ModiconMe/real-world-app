package edu.popov.domain.account.controller;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.Account;
import edu.popov.domain.account.service.AccountService;
import edu.popov.security.AccountDetails;
import edu.popov.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class AccountController {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // TODO: currentUser method

    @PostMapping("/registry")
    public AccountDTO registration(@RequestBody @Valid AccountDTO.Registration request) {
        return accountService.registry(request);
    }

    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestBody @Valid AccountDTO.Auth request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Account account = accountService.auth(request);

        final UserDetails user = AccountDetails.builder().id(account.getId()).email(account.getEmail())
                .password(account.getPassword()).build();
        if (user != null) {
            return ResponseEntity.ok(jwtUtils.generateToken(user));
        }
        return ResponseEntity.status(400).body("Some error has occurred");
    }

    @PutMapping
    public AccountDTO update(
            @RequestBody @Valid AccountDTO.Update request,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return accountService.update(accountDetails.getId(), request);
    }

}
