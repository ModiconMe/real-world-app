package edu.popov.domain.profile.service;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.domain.profile.dto.ProfileDTO;
import edu.popov.domain.profile.dto.ProfileMapper;
import edu.popov.domain.profile.entity.FollowRelationEntity;
import edu.popov.domain.profile.entity.FollowRelationId;
import edu.popov.domain.profile.repository.FollowRelationRepository;
import edu.popov.utils.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FollowRelationRepository followRelationRepository;

    @Mock
    private ProfileMapper mapper;

    private ProfileServiceImpl underTest;

    private static final String ACCOUNT_NOT_FOUND_BY_USERNAME = "Account with username %s is not exist";
    private static final String ACCOUNT_NOT_FOUND_BY_ID = "Account with id %d is not exist";

    @BeforeEach
    void setUp() {
        underTest = new ProfileServiceImpl(accountRepository, followRelationRepository, mapper);
    }

    @Test
    void itShouldGetAccountById() {
        // given
        long id = 1L;
        AccountEntity account = AccountEntity.builder()
                .id(id)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        // when
        AccountEntity expected = underTest.getAccountById(id);
        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(accountRepository, times(1)).findById(captor.capture());
        assertThat(expected.getId()).isEqualTo(captor.getValue());
    }

    @Test
    void itShouldNotGetAccountById_whenIdIsNotExists() {
        // given
        long id = 1L;
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getAccountById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ACCOUNT_NOT_FOUND_BY_ID, id);
    }

    @Test
    void itShouldGetAccountByUsername() {
        // given
        String username = "user1";
        AccountEntity account = AccountEntity.builder()
                .username(username)
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));

        // when
        AccountEntity expected = underTest.getAccountByUsername(username);
        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(accountRepository, times(1)).findByUsername(captor.capture());
        assertThat(expected.getUsername()).isEqualTo(captor.getValue());
    }

    @Test
    void itShouldNotGetAccountByUsername_whenUsernameIsNotExists() {
        // given
        String username = "user1";
        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getAccountByUsername(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ACCOUNT_NOT_FOUND_BY_USERNAME, username);
    }

    @Test
    void itShouldGetProfile() {
        // given
        AccountEntity accountToFollow = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountEntity userAccount = AccountEntity.builder()
                .id(2L)
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();
        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(accountToFollow.getId())
                .userAccountId(userAccount.getId())
                .build();
        FollowRelationEntity followRelation = FollowRelationEntity.builder()
                .id(followRelationId)
                .accountToFollow(accountToFollow)
                .userAccount(userAccount)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUsername("user1")).thenReturn(Optional.of(accountToFollow));
        when(mapper.mapToProfileDTO(accountToFollow)).thenReturn(profileDTO);
        when(followRelationRepository.findById(followRelationId)).thenReturn(Optional.of(followRelation));

        // when
        ProfileDTO profile = underTest.getProfile(accountToFollow.getUsername(), userAccount.getId());

        // then
        assertThat(profile.isFollowing()).isEqualTo(true);
    }

    @Test
    void itShouldGetProfile_whenFollowing() {
        // given
        AccountEntity accountToFollow = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountEntity userAccount = AccountEntity.builder()
                .id(2L)
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();
        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(accountToFollow.getId())
                .userAccountId(userAccount.getId())
                .build();

        when(accountRepository.findByUsername("user1")).thenReturn(Optional.of(accountToFollow));
        when(mapper.mapToProfileDTO(accountToFollow)).thenReturn(profileDTO);
        when(followRelationRepository.findById(followRelationId)).thenReturn(Optional.empty());

        // when
        ProfileDTO profile = underTest.getProfile(accountToFollow.getUsername(), userAccount.getId());

        // then
        assertThat(profile.isFollowing()).isEqualTo(false);
    }

    @Test
    void itShouldReturnTrueFollowingProfileDTO_whenAlreadyFollow() {
        // given
        AccountEntity accountToFollow = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountEntity userAccount = AccountEntity.builder()
                .id(2L)
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();
        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(accountToFollow.getId())
                .userAccountId(userAccount.getId())
                .build();
        FollowRelationEntity followRelation = FollowRelationEntity.builder()
                .id(followRelationId)
                .accountToFollow(accountToFollow)
                .userAccount(userAccount)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUsername("user1")).thenReturn(Optional.of(accountToFollow));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(userAccount));
        when(mapper.mapToProfileDTO(accountToFollow)).thenReturn(profileDTO);
        when(followRelationRepository.findById(followRelationId)).thenReturn(Optional.of(followRelation));

        // when
        ProfileDTO profile = underTest.followProfile(accountToFollow.getUsername(), userAccount.getId());

        // then
        assertThat(profile.isFollowing()).isEqualTo(true);
    }

    @Test
    void itShouldReturnTrueFollowingProfileDTO_whenFollow() {
        // given
        AccountEntity accountToFollow = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountEntity userAccount = AccountEntity.builder()
                .id(2L)
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();
        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(accountToFollow.getId())
                .userAccountId(userAccount.getId())
                .build();

        when(accountRepository.findByUsername("user1")).thenReturn(Optional.of(accountToFollow));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(userAccount));
        when(mapper.mapToProfileDTO(accountToFollow)).thenReturn(profileDTO);
        when(followRelationRepository.findById(followRelationId)).thenReturn(Optional.empty());

        // when
        ProfileDTO profile = underTest.followProfile(accountToFollow.getUsername(), userAccount.getId());

        // then
        assertThat(profile.isFollowing()).isEqualTo(true);
    }

    @Test
    void itShouldReturnFalseFollowingProfileDTO_whenUnfollow() {
        // given
        AccountEntity accountToFollow = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountEntity userAccount = AccountEntity.builder()
                .id(2L)
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();
        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(accountToFollow.getId())
                .userAccountId(userAccount.getId())
                .build();
        FollowRelationEntity followRelation = FollowRelationEntity.builder()
                .id(followRelationId)
                .accountToFollow(accountToFollow)
                .userAccount(userAccount)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUsername("user1")).thenReturn(Optional.of(accountToFollow));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(userAccount));
        when(mapper.mapToProfileDTO(accountToFollow)).thenReturn(profileDTO);
        when(followRelationRepository.findById(followRelationId)).thenReturn(Optional.of(followRelation));

        // when
        ProfileDTO profile = underTest.unfollowProfile(accountToFollow.getUsername(), userAccount.getId());

        // then
        assertThat(profile.isFollowing()).isEqualTo(false);
    }

    @Test
    void itShouldReturnFalseFollowingProfileDTO_whenAlreadyUnfollow() {
        // given
        AccountEntity accountToFollow = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountEntity userAccount = AccountEntity.builder()
                .id(2L)
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();
        FollowRelationId followRelationId = FollowRelationId.builder()
                .accountToFollowId(accountToFollow.getId())
                .userAccountId(userAccount.getId())
                .build();
        FollowRelationEntity followRelation = FollowRelationEntity.builder()
                .id(followRelationId)
                .accountToFollow(accountToFollow)
                .userAccount(userAccount)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUsername("user1")).thenReturn(Optional.of(accountToFollow));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(userAccount));
        when(mapper.mapToProfileDTO(accountToFollow)).thenReturn(profileDTO);
        when(followRelationRepository.findById(followRelationId)).thenReturn(Optional.empty());

        // when
        ProfileDTO profile = underTest.unfollowProfile(accountToFollow.getUsername(), userAccount.getId());

        // then
        assertThat(profile.isFollowing()).isEqualTo(false);
    }

    @Test
    void itShouldGetFollowers() {
        // given
        AccountEntity userAccount = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        AccountEntity follower = AccountEntity.builder()
                .id(2L)
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user2")
                .build();
        when(mapper.mapToProfileDTO(follower)).thenReturn(profileDTO);
        when(accountRepository.findById(userAccount.getId())).thenReturn(Optional.of(userAccount));
        when(followRelationRepository.findFollowers(userAccount)).thenReturn(List.of(follower));

        // when
        List<ProfileDTO> followers = underTest.getFollowers(userAccount.getId());

        // then
        assertThat(followers).isNotEmpty();
        assertThat(followers.get(0)).isEqualTo(profileDTO);
    }

    @Test
    void itShouldNotGetFollowings() {
        // given
        AccountEntity userAccount = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        when(accountRepository.findById(userAccount.getId())).thenReturn(Optional.of(userAccount));
        when(followRelationRepository.findFollowings(userAccount)).thenReturn(Collections.emptyList());

        // when
        List<ProfileDTO> followings = underTest.getFollowings(userAccount.getId());

        // then
        assertThat(followings).isEmpty();
    }
}