package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.Account;

public interface AccountService {

    AccountDTO registry(AccountDTO.Registration request);
    Account auth(AccountDTO.Auth request);
    AccountDTO update(Long id, AccountDTO.Update request);

}