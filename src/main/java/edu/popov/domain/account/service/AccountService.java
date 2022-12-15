package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.Account;
import org.springframework.security.core.userdetails.UserDetails;

public interface AccountService {

    AccountDTO registry(AccountDTO.Registration request);
    String auth(AccountDTO.Auth request);
    AccountDTO update(Long id, AccountDTO.Update request);
    UserDetails currentUser();
}