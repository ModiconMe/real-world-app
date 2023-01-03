package edu.popov.domain.profile.service;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.domain.profile.dto.ProfileDTO;
import edu.popov.domain.profile.dto.ProfileMapper;
import edu.popov.domain.profile.entity.FollowRelationEntity;
import edu.popov.domain.profile.entity.FollowRelationId;
import edu.popov.domain.profile.repository.FollowRelationRepository;
import edu.popov.utils.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final AccountRepository accountRepository;
    private final FollowRelationRepository followRelationRepository;
    private final ProfileMapper mapper;

    private static final String ACCOUNT_NOT_FOUND_BY_USERNAME = "Account with username %s is not exist";
    private static final String ACCOUNT_NOT_FOUND_BY_ID = "Account with id %d is not exist";

    /**
     * Show profile info
     */
    @Override
    public ProfileDTO getProfile(String username, Long id) {
        AccountEntity profile = getAccountByUsername(username);
        ProfileDTO profileDTO = mapper.mapToProfileDTO(profile);

        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(profile.getId())
                .userAccountId(id)
                .build();

        Optional<FollowRelationEntity> optionalFollowRelation =
                followRelationRepository.findById(followRelationId);
        if (optionalFollowRelation.isPresent())
            profileDTO.setFollowing(true);

        return profileDTO;
    }

    /**
     * Follow profile and now profile updated will be in our feed.
     */
    @Override
    @Transactional
    public ProfileDTO followProfile(String username, Long id) {
        AccountEntity profile = getAccountByUsername(username);
        AccountEntity userAccount = getAccountById(id);

        ProfileDTO followedProfile = mapper.mapToProfileDTO(profile);
        followedProfile.setFollowing(true);

        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(profile.getId())
                .userAccountId(userAccount.getId())
                .build();

        // if already follow then just return
        if (followRelationRepository.findById(followRelationId).isPresent())
            return followedProfile;

        FollowRelationEntity followRelation = FollowRelationEntity.builder()
                .id(followRelationId)
                .accountToFollow(profile)
                .userAccount(userAccount)
                .createdAt(LocalDateTime.now())
                .build();

        followRelationRepository.save(followRelation);

        log.info("account {} follow account {}", profile.getUsername(), userAccount.getUsername());
        return followedProfile;
    }

    /**
     * Unfollow profile.
     */
    @Override
    @Transactional
    public ProfileDTO unfollowProfile(String username, Long id) {
        AccountEntity profile = getAccountByUsername(username);
        AccountEntity userAccount = getAccountById(id);

        ProfileDTO unfollowedProfile = mapper.mapToProfileDTO(profile);
        unfollowedProfile.setFollowing(false);

        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(profile.getId())
                .userAccountId(userAccount.getId())
                .build();

        System.out.println(followRelationId);
        // if already unfollow then just return
        if (followRelationRepository.findById(followRelationId).isEmpty())
            return unfollowedProfile;

        followRelationRepository.deleteById(followRelationId);

        log.info("account {} unfollow account {}", profile.getUsername(), userAccount.getUsername());
        return unfollowedProfile;
    }

    @Override
    public AccountEntity getAccountById(Long id) {
        Optional<AccountEntity> optionalProfile = accountRepository.findById(id);
        if(optionalProfile.isEmpty())
            throw new NotFoundException(format(ACCOUNT_NOT_FOUND_BY_ID, id));

        return optionalProfile.get();
    }

    @Override
    public AccountEntity getAccountByUsername(String username) {
        Optional<AccountEntity> optionalProfile = accountRepository.findByUsername(username);
        if(optionalProfile.isEmpty())
            throw new NotFoundException(format(ACCOUNT_NOT_FOUND_BY_USERNAME, username));

        return optionalProfile.get();
    }

    /**
     * Get users followers.
     */
    @Override
    @Transactional
    public List<ProfileDTO> getFollowers(Long id) {
        return followRelationRepository.findFollowers(getAccountById(id))
                .stream().map(mapper::mapToProfileDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get users followings.
     */
    @Override
    public List<ProfileDTO> getFollowings(Long id) {
        return followRelationRepository.findFollowings(getAccountById(id))
                .stream().map(mapper::mapFollowingsToProfileDTO)
                .collect(Collectors.toList());
    }
}