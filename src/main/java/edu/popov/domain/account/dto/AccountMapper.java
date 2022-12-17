package edu.popov.domain.account.dto;

import edu.popov.domain.account.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountMapper {

    private final ModelMapper mapper;

    public AccountEntity mapToAccount(AccountDTO.Registration registration) {
        return mapper.map(registration, AccountEntity.class);
    }

    public AccountDTO mapToAccountDTO(AccountEntity account) {
        return mapper.map(account, AccountDTO.class);
    }
}
