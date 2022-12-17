package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;

public interface AccountService {

    AccountDTO registry(AccountDTO.Registration request);
    AccountDTO auth(AccountDTO.Auth request);
    AccountDTO update(Long id, AccountDTO.Update request);
    AccountDTO currentUser(Long userId);

}