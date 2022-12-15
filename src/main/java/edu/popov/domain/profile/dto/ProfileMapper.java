package edu.popov.domain.profile.dto;

import edu.popov.domain.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileMapper {

    private final ModelMapper mapper;

    public ProfileDTO mapToProfileDTO(Account account) {
        return mapper.map(account, ProfileDTO.class);
    }

    public ProfileDTO mapFollowingsToProfileDTO(Account account) {
        ProfileDTO follow = mapper.map(account, ProfileDTO.class);
        follow.setFollowing(true);
        return follow;
    }
}
