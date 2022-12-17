package edu.popov.domain.profile.dto;

import edu.popov.domain.account.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileMapper {

    private final ModelMapper mapper;

    public ProfileDTO mapToProfileDTO(AccountEntity account) {
        return mapper.map(account, ProfileDTO.class);
    }

    /**
     * Return users followings -> field following=true
     */
    public ProfileDTO mapFollowingsToProfileDTO(AccountEntity account) {
        ProfileDTO follow = mapper.map(account, ProfileDTO.class);
        follow.setFollowing(true);
        return follow;
    }
}
