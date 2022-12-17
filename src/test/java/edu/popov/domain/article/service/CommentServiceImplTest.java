package edu.popov.domain.article.service;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.article.dto.CommentDTO;
import edu.popov.domain.article.dto.CommentMapper;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.CommentEntity;
import edu.popov.domain.article.repository.ArticleRepository;
import edu.popov.domain.article.repository.CommentRepository;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.utils.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ProfileService profileService;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CommentMapper commentMapper;

    private CommentService underTest;

    private static final String ARTICLE_NOT_FOUND_BY_SLUG = "Article with slug %s is not found";
    private static final String COMMENT_NOT_FOUND_BY_SLUG_AND_ID = "Comment with id %d of article with slug %s is not found";

    @BeforeEach
    void setUp() {
        underTest = new CommentServiceImpl(commentRepository, profileService, articleRepository, commentMapper);
    }

    @Test
    void itShouldAddComment() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentDTO.Create create = CommentDTO.Create.builder()
                .body("body")
                .build();
        CommentEntity commentEntity = CommentEntity.builder()
                .body("body")
                .article(article)
                .account(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentDTO commentDTO = CommentDTO.builder()
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));
        when(profileService.getAccountById(account.getId())).thenReturn(account);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(commentEntity);
        when(commentMapper.mapToCommentDTO(any(CommentEntity.class))).thenReturn(commentDTO);

        // when
        underTest.addComment(article.getSlug(), create, account.getId());

        // then
        verify(articleRepository, times(1)).findBySlug(article.getSlug());
        verify(profileService, times(1)).getAccountById(account.getId());
        verify(commentRepository, times(1)).save(any(CommentEntity.class));
        verify(commentMapper, times(1)).mapToCommentDTO(any(CommentEntity.class));
    }

    @Test
    void itShouldNotAddComment_whenArticleSlugIsNotExists() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentDTO.Create create = CommentDTO.Create.builder()
                .body("body")
                .build();

        when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.addComment(article.getSlug(), create, account.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ARTICLE_NOT_FOUND_BY_SLUG, article.getSlug());
    }

    @Test
    void itShouldGetComment() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentEntity commentEntity = CommentEntity.builder()
                .body("body")
                .article(article)
                .account(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentDTO commentDTO = CommentDTO.builder()
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));
        when(commentRepository.findByArticle(article)).thenReturn(List.of(commentEntity));
        when(commentMapper.mapToCommentDTOList(List.of(commentEntity))).thenReturn(List.of(commentDTO));

        // when
        underTest.getComments(article.getSlug());

        // then
        verify(articleRepository, times(1)).findBySlug(article.getSlug());
        verify(commentRepository, times(1)).findByArticle(article);
        verify(commentMapper, times(1)).mapToCommentDTOList(List.of(commentEntity));
    }

    @Test
    void itShouldNotGetComment_whenArticleNotFound() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getComments(article.getSlug()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ARTICLE_NOT_FOUND_BY_SLUG, article.getSlug());
    }

    @Test
    void itShouldDeleteComment() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentEntity commentEntity = CommentEntity.builder()
                .body("body")
                .article(article)
                .account(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentDTO commentDTO = CommentDTO.builder()
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));
        when(commentRepository.findByIdAndArticle(commentEntity.getId(), article)).thenReturn(Optional.of(commentEntity));
        when(commentMapper.mapToCommentDTO(commentEntity)).thenReturn(commentDTO);

        // when
        underTest.deleteComment(article.getSlug(), commentEntity.getId());

        // then
        verify(articleRepository, times(1)).findBySlug(article.getSlug());
        verify(commentRepository, times(1)).findByIdAndArticle(commentEntity.getId(), article);
        verify(commentRepository, times(1)).deleteById(commentEntity.getId());
        verify(commentMapper, times(1)).mapToCommentDTO(commentEntity);
    }

    @Test
    void itShouldNotDeleteComment_whenArticleNotFound() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentEntity commentEntity = CommentEntity.builder()
                .body("body")
                .article(article)
                .account(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteComment(article.getSlug(), commentEntity.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ARTICLE_NOT_FOUND_BY_SLUG, article.getSlug());
    }

    @Test
    void itShouldNotDeleteComment_whenCommentNotFound() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ArticleEntity article = ArticleEntity.builder()
                .id(1L)
                .slug("article-1")
                .title("article 1")
                .description("description")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CommentEntity commentEntity = CommentEntity.builder()
                .body("body")
                .article(article)
                .account(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));
        when(commentRepository.findByIdAndArticle(commentEntity.getId(), article)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteComment(article.getSlug(), commentEntity.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(COMMENT_NOT_FOUND_BY_SLUG_AND_ID, commentEntity.getId(), article.getSlug());
    }

}