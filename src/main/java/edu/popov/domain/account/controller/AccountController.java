package edu.popov.domain.account.controller;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.service.AccountService;
import edu.popov.security.AccountDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public AccountDTO currentUser(@AuthenticationPrincipal AccountDetails accountDetails) {
        return accountService.currentUser(accountDetails.id());
    }

    @PutMapping
    public AccountDTO update(
            @RequestBody @Valid AccountDTO.Update request,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return accountService.update(accountDetails.id(), request);
    }

}
