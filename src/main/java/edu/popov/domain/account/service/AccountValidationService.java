package edu.popov.domain.account.service;

import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.AccountEntity;

public interface AccountValidationService {

    Boolean registryValidation(AccountDTO.Registration request);
    AccountEntity authValidation(AccountDTO.Auth request);
    AccountEntity updateValidation(Long id, AccountDTO.Update request);

}
