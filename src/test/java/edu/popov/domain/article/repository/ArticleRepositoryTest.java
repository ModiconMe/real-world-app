package edu.popov.domain.article.repository;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.FavoriteEntity;
import edu.popov.domain.article.entity.FavoriteEntityId;
import edu.popov.domain.profile.entity.FollowRelationEntity;
import edu.popov.domain.profile.entity.FollowRelationId;
import edu.popov.domain.profile.repository.FollowRelationRepository;
import edu.popov.domain.tag.entity.TagEntity;
import edu.popov.domain.tag.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArticleRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FollowRelationRepository followRelationRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository underTest;

    @Test
    void itShouldFindBySlug() {
        // given
        AccountEntity account = AccountEntity.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        String slug = "article-1";
        ArticleEntity article = ArticleEntity.builder()
                .slug(slug)
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        accountRepository.saveAndFlush(account);
        underTest.save(article);

        // when
        Optional<ArticleEntity> expected = underTest.findBySlug(slug);

        // then
        assertThat(expected).isPresent();
        assertThat(expected.get()).isEqualTo(article);
    }

    @Test
    void itShouldDeleteBySlug() {
        // given
        AccountEntity account = AccountEntity.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        String slug = "article-1";
        ArticleEntity article = ArticleEntity.builder()
                .slug(slug)
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        accountRepository.saveAndFlush(account);
        underTest.save(article);

        // when
        Long expected = underTest.deleteBySlug(slug);

        // then
        Optional<ArticleEntity> optionalArticle = underTest.findBySlug(slug);
        assertThat(optionalArticle).isEmpty();
        assertThat(expected).isEqualTo(1L);
    }

    @Test
    void itShouldFindByTagAndAuthorAndUserId() {
        // given
        String filterAuthor = "user1";
        AccountEntity account1 = AccountEntity.builder()
                .username(filterAuthor)
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
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        ArticleEntity article = ArticleEntity.builder()
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account1)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        String filterTag = "tag1";
        TagEntity tag = TagEntity.builder()
                .tagName(filterTag)
                .build();
        article.addTag(tag);

        accountRepository.saveAndFlush(account1);
        accountRepository.saveAndFlush(account2);

        FavoriteEntityId favoriteEntityId = FavoriteEntityId.builder()
                .articleId(account1.getId())
                .accountId(account2.getId())
                .build();

        FavoriteEntity favoriteEntity = FavoriteEntity.builder()
                .id(favoriteEntityId)
                .article(article)
                .account(account2)
                .build();
        article.addFavorite(favoriteEntity);

        underTest.saveAndFlush(article);
        favoriteRepository.save(favoriteEntity);

        // when
        List<ArticleEntity> expected = underTest.findByFilter(filterTag, filterAuthor, account2, null);

        // then
        assertThat(expected).isNotEmpty();
        assertThat(expected.get(0)).isEqualTo(article);
    }

    @Test
    void itShouldFindByFeed() {
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
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();

        ArticleEntity article1 = ArticleEntity.builder()
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account1)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        ArticleEntity article2 = ArticleEntity.builder()
                .slug("article-2")
                .title("article 2")
                .description("description")
                .body("body")
                .author(account2)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        TagEntity tag1 = TagEntity.builder()
                .tagName("tag1")
                .build();
        article1.addTag(tag1);
        TagEntity tag2 = TagEntity.builder()
                .tagName("tag2")
                .build();
        article2.addTag(tag2);

        accountRepository.saveAndFlush(account1);
        accountRepository.saveAndFlush(account2);

        FollowRelationEntity followRelation = FollowRelationEntity.builder()
                .id(FollowRelationId.builder()
                        .accountToFollowId(account2.getId())
                        .userAccountId(account1.getId())
                        .build())
                .accountToFollow(account2)
                .userAccount(account1)
                .createdAt(LocalDateTime.now())
                .build();
        followRelationRepository.saveAndFlush(followRelation);

        underTest.saveAndFlush(article1);
        underTest.saveAndFlush(article2);

        // when
        List<ArticleEntity> expected = underTest.findByFeed(account1, null);

        // then
        assertThat(expected).isNotEmpty();
        assertThat(expected.get(0)).isEqualTo(article2);
    }
}