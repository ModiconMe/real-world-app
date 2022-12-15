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
public class AuthenticateController {

    private final AccountService accountService;

    @PostMapping
    public AccountDTO registration(@RequestBody @Valid AccountDTO.Registration request) {
        return accountService.registry(request);
    }

    @PostMapping("/login")
    public AccountDTO auth(@RequestBody @Valid AccountDTO.Auth request) {
        return accountService.auth(request);
    }

}
