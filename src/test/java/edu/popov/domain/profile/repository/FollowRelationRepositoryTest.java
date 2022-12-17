package edu.popov.domain.profile.repository;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.domain.profile.entity.FollowRelationEntity;
import edu.popov.domain.profile.entity.FollowRelationId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FollowRelationRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FollowRelationRepository underTest;

    @Test
    void itShouldFindRelation() {
        // given
        AccountEntity account1 = AccountEntity.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        AccountEntity account2 = AccountEntity.builder()
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .build();

        FollowRelationEntity followRelation = FollowRelationEntity.builder()
                .id(FollowRelationId.builder()
                        .accountToFollowId(1L)
                        .userAccountId(2L)
                        .build())
                .accountToFollow(account1)
                .userAccount(account2)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.saveAndFlush(account1);
        accountRepository.saveAndFlush(account2);
        underTest.save(followRelation);

        // when
        Optional<FollowRelationEntity> optionalFollowRelation =
                underTest.findByAccountToFollowAndUserAccount(account1, account2);

        // then
        assertThat(optionalFollowRelation).isPresent();
        assertThat(optionalFollowRelation.get()).isEqualTo(followRelation);
    }

    @Test
    void itShouldFindFollowers() {
        // given
        AccountEntity account1 = AccountEntity.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        AccountEntity account2 = AccountEntity.builder()
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .build();

        AccountEntity account3 = AccountEntity.builder()
                .username("user3")
                .email("user3@gmail.com")
                .password("pass3")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .build();

        FollowRelationEntity followRelation1 = FollowRelationEntity.builder()
                .id(FollowRelationId.builder()
                        .accountToFollowId(1L)
                        .userAccountId(2L)
                        .build())
                .accountToFollow(account1)
                .userAccount(account2)
                .createdAt(LocalDateTime.now())
                .build();

        FollowRelationEntity followRelation2 = FollowRelationEntity.builder()
                .id(FollowRelationId.builder()
                        .accountToFollowId(1L)
                        .userAccountId(3L)
                        .build())
                .accountToFollow(account1)
                .userAccount(account3)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.saveAndFlush(account1);
        accountRepository.saveAndFlush(account2);
        accountRepository.saveAndFlush(account3);
        underTest.save(followRelation1);
        underTest.save(followRelation2);

        // when
        List<AccountEntity> followers = underTest.findFollowers(account1);

        // then
        assertThat(followers).isNotEmpty();
        assertThat(followers.get(0)).isEqualTo(account2);
        assertThat(followers.get(1)).isEqualTo(account3);
    }

    @Test
    void itShouldFindFollowings() {
        // given
        AccountEntity account1 = AccountEntity.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        AccountEntity account2 = AccountEntity.builder()
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .build();

        AccountEntity account3 = AccountEntity.builder()
                .username("user3")
                .email("user3@gmail.com")
                .password("pass3")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 11, 11, 17, 20, 20))
                .build();

        FollowRelationEntity followRelation1 = FollowRelationEntity.builder()
                .id(FollowRelationId.builder()
                        .accountToFollowId(2L)
                        .userAccountId(1L)
                        .build())
                .accountToFollow(account2)
                .userAccount(account1)
                .createdAt(LocalDateTime.now())
                .build();

        FollowRelationEntity followRelation2 = FollowRelationEntity.builder()
                .id(FollowRelationId.builder()
                        .accountToFollowId(3L)
                        .userAccountId(1L)
                        .build())
                .accountToFollow(account3)
                .userAccount(account1)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.saveAndFlush(account1);
        accountRepository.saveAndFlush(account2);
        accountRepository.saveAndFlush(account3);
        underTest.save(followRelation1);
        underTest.save(followRelation2);

        // when
        List<AccountEntity> followers = underTest.findFollowings(account1);

        // then
        assertThat(followers).isNotEmpty();
        assertThat(followers.get(0)).isEqualTo(account2);
        assertThat(followers.get(1)).isEqualTo(account3);
    }
}