package edu.popov.domain.account.controller;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
