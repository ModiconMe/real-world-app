package edu.popov.domain.profile.service;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.profile.dto.ProfileDTO;

import java.util.List;

public interface ProfileService {

    ProfileDTO getProfile(String username, Long id);

    ProfileDTO followProfile(String username, Long id);

    ProfileDTO unfollowProfile(String username, Long id);

    AccountEntity getAccountById(Long id);

    AccountEntity getAccountByUsername(String username);

    List<ProfileDTO> getFollowers(Long id);

    List<ProfileDTO> getFollowings(Long id);

}
