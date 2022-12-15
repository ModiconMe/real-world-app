package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.Account;

public interface AccountValidationService {

    Boolean registryValidation(AccountDTO.Registration request);
    Account authValidation(AccountDTO.Auth request);
    Account updateValidation(Long id, AccountDTO.Update request);

}
