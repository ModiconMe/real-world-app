package edu.popov.domain.article.service;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.article.dto.ArticleDTO;
import edu.popov.domain.article.dto.ArticleMapper;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.FavoriteEntity;
import edu.popov.domain.article.entity.FavoriteEntityId;
import edu.popov.domain.article.model.ArticleFilter;
import edu.popov.domain.article.model.FeedParams;
import edu.popov.domain.article.repository.ArticleRepository;
import edu.popov.domain.article.repository.FavoriteRepository;
import edu.popov.domain.article.repository.OffsetBasedPageRequest;
import edu.popov.domain.profile.dto.ProfileDTO;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.security.AccountDetails;
import edu.popov.utils.exception.BadRequestException;
import edu.popov.utils.exception.ForbiddenException;
import edu.popov.utils.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private ArticleMapper articleMapper;

    private ArticleService underTest;

    private static final String ARTICLE_NOT_FOUND_BY_SLUG = "Article with slug %s is not found";
    private static final String ARTICLE_ALREADY_EXISTS_BY_SLUG = "Article with slug %s is already exist";
    private static final String IS_NOT_AN_OWNER_OF_ARTICLE = "Article with slug %s is not owned by %s";

    @BeforeEach
    void setUp() {
        underTest = new ArticleServiceImpl(articleRepository, profileService, articleMapper, favoriteRepository);
    }

    @Test
    void itShouldCreateArticle() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();
        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .build();

        when(articleRepository.findBySlug("title")).thenReturn(Optional.empty());
        when(profileService.getAccountById(1L)).thenReturn(account);
        when(articleRepository.save(any(ArticleEntity.class))).thenReturn(articleEntity);
        when(articleMapper.mapToArticleDTO(any(ArticleEntity.class), anyLong())).thenReturn(articleDTO);

        // when
        underTest.createArticle(articleDTO, 1L);

        // then
        verify(articleRepository, times(1)).findBySlug("title");
        verify(profileService, times(1)).getAccountById(1L);
        verify(articleRepository, times(1)).save(any(ArticleEntity.class));
        verify(articleMapper, times(1)).mapToArticleDTO(any(ArticleEntity.class), anyLong());
    }

    @Test
    void itShouldNotCreateArticle_whenArticleAlreadyExists() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();
        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .build();

        when(articleRepository.findBySlug("title")).thenReturn(Optional.of(articleEntity));

        // when
        // then
        assertThatThrownBy(() -> underTest.createArticle(articleDTO, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ARTICLE_ALREADY_EXISTS_BY_SLUG, "title");
    }

    @Test
    void itShouldGetArticleBySlug() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        String slug = "title";
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug(slug)
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .build();

        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(articleEntity));
        when(articleMapper.mapToArticleDTO(articleEntity, 1L)).thenReturn(articleDTO);

        // when
        ArticleDTO expected = underTest.getArticleBySlug(slug, 1L);

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(articleRepository).findBySlug(captor.capture());
        assertThat(expected.getSlug()).isEqualTo(captor.getValue());
    }

    @Test
    void itShouldNotGetArticleBySlug_whenSlugIsNotExists() {
        // given
        String slug = "slug";
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getArticleBySlug(slug, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ARTICLE_NOT_FOUND_BY_SLUG, slug);
    }

    @Test
    void itShouldUpdateArticle() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity1 = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .build();
        ArticleEntity articleEntity2 = ArticleEntity.builder()
                .slug("title-title")
                .title("title title")
                .description("desc1")
                .body("body1")
                .author(account)
                .build();
        ArticleDTO.Update articleDTOUpdate = ArticleDTO.Update.builder()
                .title("title title")
                .description("desc1")
                .body("body1")
                .build();
        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .build();

        when(articleRepository.findBySlug("title")).thenReturn(Optional.of(articleEntity1));
        when(articleRepository.save(articleEntity2)).thenReturn(articleEntity2);
        when(articleMapper.mapToArticleDTO(articleEntity2, 1L)).thenReturn(articleDTO);

        // when
        underTest.updateArticle("title", articleDTOUpdate, 1L);

        // then
        verify(articleRepository, times(1)).findBySlug("title");
        verify(articleRepository, times(1)).save(articleEntity2);
        verify(articleMapper, times(1)).mapToArticleDTO(articleEntity2, 1L);
    }

    @Test
    void itShouldNotUpdateArticle_whenSlugIsNotExists() {
        // given
        String slug = "title";
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateArticle(slug, any(), 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ARTICLE_NOT_FOUND_BY_SLUG, slug);
    }

    @Test
    void itShouldDeleteArticleBySlug() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity1 = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .build();
        String slug = "title";
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(articleEntity1));
        when(articleRepository.deleteBySlug(slug)).thenReturn(1L);

        // when
        underTest.deleteArticle(slug, "user1@gmail.com");

        // then
        verify(articleRepository, times(1)).findBySlug(slug);
        verify(articleRepository, times(1)).deleteBySlug(slug);
    }

    @Test
    void itShouldNotDeleteArticleBySlug_whenSlugIsNotFound() {
        // given
        String slug = "title";
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteArticle(slug, "user1"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ARTICLE_NOT_FOUND_BY_SLUG, slug);
    }

    @Test
    void itShouldNotDeleteArticleBySlug_whenUserIsNotAnOwner() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity1 = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .build();
        String slug = "title";
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(articleEntity1));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteArticle(slug, "user2"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(IS_NOT_AN_OWNER_OF_ARTICLE, slug, "user2");
    }

    @Test
    void itShouldGetArticlesByFilter() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity1 = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .build();
        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .build();
        ArticleFilter articleFilter = ArticleFilter.builder()
                .author("user1")
                .favorited("user1")
                .tag("tag1")
                .limit(1)
                .offset(1)
                .build();
        AccountDetails accountDetails = AccountDetails.builder()
                .id(1L)
                .build();
        Pageable pageable = new OffsetBasedPageRequest(
                articleFilter.getLimit(), articleFilter.getOffset(), Sort.by(Sort.Order.desc("createdAt")));

        when(profileService.getAccountByUsername(articleFilter.getFavorited())).thenReturn(account);
        List<ArticleEntity> articleEntities = List.of(articleEntity1);
        when(articleRepository.findByFilter(articleFilter.getTag(), articleFilter.getAuthor(), account, pageable)).thenReturn(articleEntities);
        when(articleMapper.mapToArticleDTOList(articleEntities, 1L)).thenReturn(List.of(articleDTO));

        // when
        underTest.getArticlesByFilter(articleFilter, accountDetails);

        // then
        verify(profileService, times(1)).getAccountByUsername(articleFilter.getFavorited());
        verify(articleRepository, times(1)).findByFilter(articleFilter.getTag(), articleFilter.getAuthor(), account, pageable);
        verify(articleMapper, times(1)).mapToArticleDTOList(articleEntities, 1L);
    }

    @Test
    void itShouldGetArticlesByFilter_whenUserIsNotAuth() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity1 = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .build();
        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .build();
        ArticleFilter articleFilter = ArticleFilter.builder()
                .author("user1")
                .favorited("user1")
                .tag("tag1")
                .limit(1)
                .offset(1)
                .build();
        Pageable pageable = new OffsetBasedPageRequest(
                articleFilter.getLimit(), articleFilter.getOffset(), Sort.by(Sort.Order.desc("createdAt")));
        AccountDetails accountDetails = AccountDetails.builder()
                .id(1L)
                .build();

        when(profileService.getAccountByUsername(articleFilter.getFavorited())).thenReturn(null);
        List<ArticleEntity> articleEntities = List.of(articleEntity1);
        when(articleRepository.findByFilter(articleFilter.getTag(), articleFilter.getAuthor(), null, pageable)).thenReturn(articleEntities);
        when(articleMapper.mapToArticleDTOList(articleEntities, 1L)).thenReturn(List.of(articleDTO));

        // when
        underTest.getArticlesByFilter(articleFilter, accountDetails);

        // then
        verify(profileService, times(1)).getAccountByUsername(articleFilter.getFavorited());
        verify(articleRepository, times(1)).findByFilter(articleFilter.getTag(), articleFilter.getAuthor(), null, pageable);
        verify(articleMapper, times(1)).mapToArticleDTOList(articleEntities, 1L);
    }

    @Test
    void itShouldFavoriteArticle() {
        // given
        AccountEntity user = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(user)
                .build();

        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .favorited(false)
                .build();

        FavoriteEntityId favoriteEntityId = FavoriteEntityId.builder()
                .articleId(articleEntity.getId())
                .accountId(user.getId())
                .build();

        FavoriteEntity favorite = FavoriteEntity.builder()
                .id(favoriteEntityId)
                .article(articleEntity)
                .account(user)
                .build();

        when(articleRepository.findBySlug(articleEntity.getSlug())).thenReturn(Optional.of(articleEntity));
        when(profileService.getAccountById(user.getId())).thenReturn(user);
        when(favoriteRepository.findById(favoriteEntityId)).thenReturn(Optional.empty());
        when(favoriteRepository.save(favorite)).thenReturn(favorite);
        when(articleMapper.mapToArticleDTO(articleEntity, 1L)).thenReturn(articleDTO);

        // when
        underTest.favoriteArticle(articleEntity.getSlug(), 1L);

        // then
        verify(articleRepository, times(2)).findBySlug(articleEntity.getSlug());
        verify(profileService, times(1)).getAccountById(user.getId());
        verify(favoriteRepository, times(1)).findById(favoriteEntityId);
        verify(favoriteRepository, times(1)).save(favorite);
    }

    @Test
    void itShouldNotFavoriteArticle_whenArticleNotFound() {
        // given
        AccountEntity user = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(user)
                .build();

        when(articleRepository.findBySlug(articleEntity.getSlug())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.favoriteArticle(articleEntity.getSlug(), user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ARTICLE_NOT_FOUND_BY_SLUG, articleEntity.getSlug());
    }

    @Test
    void itShouldNotFavoriteArticle_whenAlreadyFavorite() {
        // given
        AccountEntity user = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(user)
                .build();

        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .favorited(false)
                .build();

        FavoriteEntityId favoriteEntityId = FavoriteEntityId.builder()
                .articleId(articleEntity.getId())
                .accountId(user.getId())
                .build();

        FavoriteEntity favorite = FavoriteEntity.builder()
                .id(favoriteEntityId)
                .article(articleEntity)
                .account(user)
                .build();

        when(articleRepository.findBySlug(articleEntity.getSlug())).thenReturn(Optional.of(articleEntity));
        when(profileService.getAccountById(user.getId())).thenReturn(user);
        when(favoriteRepository.findById(favoriteEntityId)).thenReturn(Optional.of(favorite));
        when(articleMapper.mapToArticleDTO(articleEntity, 1L)).thenReturn(articleDTO);

        // when
        underTest.favoriteArticle(articleEntity.getSlug(), 1L);

        // then
        verify(articleRepository, times(2)).findBySlug(articleEntity.getSlug());
        verify(profileService, times(1)).getAccountById(user.getId());
        verify(favoriteRepository, times(1)).findById(favoriteEntityId);
        verify(favoriteRepository, times(0)).save(favorite);
    }

    @Test
    void itShouldUnfavoriteArticle() {
        // given
        AccountEntity user = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(user)
                .build();

        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .favorited(false)
                .build();

        FavoriteEntityId favoriteEntityId = FavoriteEntityId.builder()
                .articleId(articleEntity.getId())
                .accountId(user.getId())
                .build();

        FavoriteEntity favorite = FavoriteEntity.builder()
                .id(favoriteEntityId)
                .article(articleEntity)
                .account(user)
                .build();

        when(articleRepository.findBySlug(articleEntity.getSlug())).thenReturn(Optional.of(articleEntity));
        when(profileService.getAccountById(user.getId())).thenReturn(user);
        when(favoriteRepository.findById(favoriteEntityId)).thenReturn(Optional.of(favorite));
        when(articleMapper.mapToArticleDTO(articleEntity, 1L)).thenReturn(articleDTO);

        // when
        underTest.unfavoriteArticle(articleEntity.getSlug(), 1L);

        // then
        verify(articleRepository, times(2)).findBySlug(articleEntity.getSlug());
        verify(profileService, times(1)).getAccountById(user.getId());
        verify(favoriteRepository, times(1)).findById(favoriteEntityId);
        verify(favoriteRepository, times(1)).deleteById(favoriteEntityId);
    }

    @Test
    void itShouldNotUnfavoriteArticle_whenArticleNotFound() {
        // given
        AccountEntity user = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(user)
                .build();

        when(articleRepository.findBySlug(articleEntity.getSlug())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.unfavoriteArticle(articleEntity.getSlug(), user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ARTICLE_NOT_FOUND_BY_SLUG, articleEntity.getSlug());
    }

    @Test
    void itShouldNotUnfavoriteArticle_whenAlreadyUnfavorited() {
        // given
        AccountEntity user = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(user)
                .build();

        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .favorited(false)
                .build();

        FavoriteEntityId favoriteEntityId = FavoriteEntityId.builder()
                .articleId(articleEntity.getId())
                .accountId(user.getId())
                .build();

        FavoriteEntity favorite = FavoriteEntity.builder()
                .id(favoriteEntityId)
                .article(articleEntity)
                .account(user)
                .build();

        when(articleRepository.findBySlug(articleEntity.getSlug())).thenReturn(Optional.of(articleEntity));
        when(profileService.getAccountById(user.getId())).thenReturn(user);
        when(favoriteRepository.findById(favoriteEntityId)).thenReturn(Optional.empty());
        when(articleMapper.mapToArticleDTO(articleEntity, 1L)).thenReturn(articleDTO);

        // when
        underTest.unfavoriteArticle(articleEntity.getSlug(), 1L);

        // then
        verify(articleRepository, times(2)).findBySlug(articleEntity.getSlug());
        verify(profileService, times(1)).getAccountById(user.getId());
        verify(favoriteRepository, times(1)).findById(favoriteEntityId);
        verify(favoriteRepository, times(0)).deleteById(favoriteEntityId);
    }

    @Test
    void itShouldGetArticlesByFeed() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArticleEntity articleEntity1 = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .build();
        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .build();
        FeedParams feedParams = FeedParams.builder()
                .limit(1)
                .offset(1)
                .build();

        Pageable pageable = new OffsetBasedPageRequest(
                feedParams.getLimit(), feedParams.getOffset(), Sort.by(Sort.Order.desc("createdAt")));

        when(profileService.getAccountById(account.getId())).thenReturn(account);
        List<ArticleEntity> articleEntities = List.of(articleEntity1);
        when(articleRepository.findByFeed(account, pageable)).thenReturn(articleEntities);
        when(articleMapper.mapToArticleDTOList(articleEntities, account.getId())).thenReturn(List.of(articleDTO));

        // when
        underTest.getArticlesByFeed(feedParams, account.getId());

        // then
        verify(profileService, times(1)).getAccountById(account.getId());
        verify(articleRepository, times(1)).findByFeed(account, pageable);
        verify(articleMapper, times(1)).mapToArticleDTOList(articleEntities, account.getId());
    }

}