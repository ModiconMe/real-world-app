package edu.popov.domain.account.controller;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.service.AccountService;
import edu.popov.security.AccountDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public UserDetails currentUser() {
        return accountService.currentUser();
    }

    @PostMapping("/registry")
    public AccountDTO registration(@RequestBody @Valid AccountDTO.Registration request) {
        return accountService.registry(request);
    }

    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestBody @Valid AccountDTO.Auth request) {
        return ResponseEntity.ok(accountService.auth(request));
    }

    @PutMapping
    public AccountDTO update(
            @RequestBody @Valid AccountDTO.Update request,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return accountService.update(accountDetails.id(), request);
    }

}
