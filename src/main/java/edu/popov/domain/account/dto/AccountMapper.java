package edu.popov.domain.account.dto;

import edu.popov.domain.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountMapper {

    private final ModelMapper mapper;

    public Account mapToAccount(AccountDTO.Registration registration) {
        return mapper.map(registration, Account.class);
    }

    public AccountDTO mapToAccountDTO(Account account) {
        return mapper.map(account, AccountDTO.class);
    }
}
