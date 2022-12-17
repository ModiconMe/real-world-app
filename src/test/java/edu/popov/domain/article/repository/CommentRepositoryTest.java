package edu.popov.domain.article.repository;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.CommentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository underTest;

    @Test
    void itShouldFindCommentsByArticle() {
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
        articleRepository.saveAndFlush(article);

        CommentEntity commentEntity = CommentEntity.builder()
                .body("body")
                .article(article)
                .account(account)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        underTest.save(commentEntity);

        // when
        List<CommentEntity> expected = underTest.findByArticle(article);

        // then
        assertThat(expected.size()).isEqualTo(1);
        assertThat(expected.get(0)).isEqualTo(commentEntity);
    }

    @Test
    void itShouldFindCommentsByArticleAndId() {
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
        articleRepository.saveAndFlush(article);

        CommentEntity commentEntity = CommentEntity.builder()
                .body("body")
                .article(article)
                .account(account)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        underTest.save(commentEntity);

        // when
        Optional<CommentEntity> expected = underTest.findByIdAndArticle(commentEntity.getId(), article);

        // then
        assertThat(expected).isPresent();
        assertThat(expected.get()).isEqualTo(commentEntity);
    }
}