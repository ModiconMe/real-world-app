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
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.domain.tag.entity.TagEntity;
import edu.popov.security.AccountDetails;
import edu.popov.utils.exception.BadRequestException;
import edu.popov.utils.exception.ForbiddenException;
import edu.popov.utils.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ProfileService profileService;
    private final ArticleMapper articleMapper;
    private final FavoriteRepository favoriteRepository;

    private static final String ARTICLE_NOT_FOUND_BY_SLUG = "Article with slug %s is not found";
    private static final String ARTICLE_ALREADY_EXISTS_BY_SLUG = "Article with slug %s is already exist";
    private static final String IS_NOT_AN_OWNER_OF_ARTICLE = "Article with slug %s is not owned by %s";

    /**
     * Create article and check that article with same slug is not exist
     */
    @Override
    @Transactional
    public ArticleDTO.SingleArticle<ArticleDTO> createArticle(ArticleDTO articleDTO, Long userId) {
        String slug = String.join("-", articleDTO.getTitle().split(" "));

        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);
        if (optionalArticle.isPresent())
            throw new BadRequestException(format(ARTICLE_ALREADY_EXISTS_BY_SLUG, slug));

        AccountEntity author = profileService.getAccountById(userId);

        List<TagEntity> tags = new ArrayList<>();
        if (Objects.nonNull(articleDTO.getTagList()))
            tags = articleDTO.getTagList().stream()
                    .map(t -> TagEntity.builder().tagName(t).build()).toList();

        ArticleEntity article = ArticleEntity.builder()
                .slug(slug)
                .title(articleDTO.getTitle())
                .description(articleDTO.getDescription())
                .body(articleDTO.getBody())
                .author(author)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .favoriteList(new ArrayList<>())
                .build();
        tags.forEach(article::addTag);

        return new ArticleDTO.SingleArticle<>(articleMapper.mapToSingleArticleDTO(
                articleRepository.save(article), userId));
    }

    /**
     * Return slug by slug
     */
    @Override
    @Transactional(readOnly = true)
    public ArticleDTO.SingleArticle<ArticleDTO> getArticleBySlug(String slug, Long userId) {
        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);
        if (optionalArticle.isEmpty())
            throw new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug));

        ArticleEntity article = optionalArticle.get();

        return new ArticleDTO.SingleArticle<>(articleMapper.mapToSingleArticleDTO(article, userId));
    }

    /**
     * Update article by slug and check, that slug with the same name is not exist
     */
    @Override
    @Transactional
    public ArticleDTO.SingleArticle<ArticleDTO> updateArticle(String slug, ArticleDTO.Update articleDTO, Long userId) {
        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);
        if (optionalArticle.isEmpty())
            throw new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug));

        ArticleEntity articleEntity = optionalArticle.get();

        if (Objects.nonNull(articleDTO.getTitle())) {
            String newSlug = String.join("-", articleDTO.getTitle().split(" "));
            articleEntity.setSlug(newSlug);
            articleEntity.setTitle(articleDTO.getTitle());
        }

        if (Objects.nonNull(articleDTO.getDescription()))
            articleEntity.setDescription(articleDTO.getDescription());
        if (Objects.nonNull(articleDTO.getBody()))
            articleEntity.setBody(articleDTO.getBody());

        return new ArticleDTO.SingleArticle<>(articleMapper.mapToSingleArticleDTO(
                articleRepository.save(articleEntity), userId
        ));
    }

    /**
     * Delete your owner article (Login required).
     */
    @Override
    @Transactional
    public void deleteArticle(String slug, String username) {
        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);

        if (optionalArticle.isEmpty())
            throw new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug));

        if (optionalArticle.get().getAuthor().getEmail().equals(username)) {
            articleRepository.deleteBySlug(slug);
            return;
        }

        throw new ForbiddenException(format(IS_NOT_AN_OWNER_OF_ARTICLE, slug, username));
    }

    /**
     * Get article by user filters (tags, author), also can set pagination and offset.
     * Favorite article other users can be seen by set favorited filter (username)
     */
    @Override
    @Transactional(readOnly = true)
    public ArticleDTO.MultipleArticle getArticlesByFilter(ArticleFilter articleFilter, AccountDetails user) {

        int limit = 20;
        int offset = 0;

        if (articleFilter.getLimit() != null)
            limit = articleFilter.getLimit();
        if (articleFilter.getOffset() != null)
            offset = articleFilter.getOffset();

        Pageable pageable = new OffsetBasedPageRequest(limit, offset, Sort.by(Sort.Order.desc("createdAt")));

        AccountEntity account = null;
        if (articleFilter.getFavorited() != null)
            account = profileService.getAccountByUsername(articleFilter.getFavorited());

        Long userId = null;
        if (user != null)
            userId = user.id();

        List<ArticleDTO> articles = articleMapper.mapToMultipleArticleDTOList(
                articleRepository.findByFilter(articleFilter.getTag(), articleFilter.getAuthor(), account, pageable),
                userId);
        return ArticleDTO.MultipleArticle.builder()
                .articles(
                        articles
                )
                .articlesCount(articles.size())
                .build();
    }

    /**
     * Add article to favorite.
     */
    @Override
    public ArticleDTO.SingleArticle<ArticleDTO> favoriteArticle(String slug, Long userId) {
        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);
        if (optionalArticle.isEmpty())
            throw new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug));

        ArticleEntity articleEntity = optionalArticle.get();
        AccountEntity user = profileService.getAccountById(userId);

        FavoriteEntityId favoriteEntityId = FavoriteEntityId.builder()
                .articleId(articleEntity.getId())
                .accountId(user.getId())
                .build();

        if (favoriteRepository.findById(favoriteEntityId).isPresent())
            return getArticleBySlug(slug, userId);

        FavoriteEntity favorite = FavoriteEntity.builder()
                .id(favoriteEntityId)
                .article(articleEntity)
                .account(user)
                .build();
        favoriteRepository.save(favorite);

        return getArticleBySlug(slug, userId);
    }

    /**
     * Remove article from favorite
     */
    @Override
    public ArticleDTO.SingleArticle<ArticleDTO> unfavoriteArticle(String slug, Long userId) {
        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);
        if (optionalArticle.isEmpty())
            throw new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug));

        ArticleEntity articleEntity = optionalArticle.get();
        AccountEntity user = profileService.getAccountById(userId);

        FavoriteEntityId favoriteEntityId = FavoriteEntityId.builder()
                .articleId(articleEntity.getId())
                .accountId(user.getId())
                .build();

        if (favoriteRepository.findById(favoriteEntityId).isEmpty())
            return getArticleBySlug(slug, userId);

        favoriteRepository.deleteById(favoriteEntityId);

        return getArticleBySlug(slug, userId);
    }

    /**
     * For authenticated users we can show feed -> recent articles of followed profiles.
     * Pagination and offset available.
     */
    @Override
    @Transactional(readOnly = true)
    public ArticleDTO.MultipleArticle getArticlesByFeed(FeedParams feedParams, Long userId) {

        int limit = 20;
        int offset = 0;

        if (feedParams.getLimit() != null)
            limit = feedParams.getLimit();
        if (feedParams.getOffset() != null)
            offset = feedParams.getOffset();

        Pageable pageable = new OffsetBasedPageRequest(limit, offset, Sort.by(Sort.Order.desc("createdAt")));

        AccountEntity user = profileService.getAccountById(userId);

        List<ArticleDTO> articles = articleMapper.mapToMultipleArticleDTOList(
                articleRepository.findByFeed(user, pageable),
                userId);
        return ArticleDTO.MultipleArticle.builder()
                .articles(articles)
                .articlesCount(articles.size())
                .build();
    }
}