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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    @Transactional(readOnly = false)
    public ArticleDTO createArticle(ArticleDTO articleDTO, Long userId) {
        String slug = String.join("-", articleDTO.getTitle().split(" "));

        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);
        if (optionalArticle.isPresent())
            throw new BadRequestException(format(ARTICLE_ALREADY_EXISTS_BY_SLUG, slug));

        AccountEntity author = profileService.getAccountById(userId);

        List<TagEntity> tags = articleDTO.getTags().stream()
                .map(t -> TagEntity.builder().tagName(t).build()).toList();

        ArticleEntity article = ArticleEntity.builder()
                .slug(slug)
                .title(articleDTO.getTitle())
                .description(articleDTO.getDescription())
                .body(articleDTO.getBody())
                .author(author)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .favoriteList(new ArrayList<>())
                .build();
        tags.forEach(article::addTag);

        return articleMapper.mapToArticleDTO(
                articleRepository.save(
                        article
                ), userId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDTO getArticleBySlug(String slug, Long userId) {
        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);
        if (optionalArticle.isEmpty())
            throw new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug));

        ArticleEntity article = optionalArticle.get();

        return articleMapper.mapToArticleDTO(article, userId);
    }

    @Override
    @Transactional(readOnly = false)
    public ArticleDTO updateArticle(String slug, ArticleDTO.Update articleDTO, Long userId) {
        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);
        if (optionalArticle.isEmpty())
            throw new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug));

        ArticleEntity articleEntity = optionalArticle.get();

        String newSlug = String.join("-", articleDTO.getTitle().split(" "));

        articleEntity.setSlug(newSlug);
        articleEntity.setTitle(articleDTO.getTitle());
        articleEntity.setDescription(articleDTO.getDescription());
        articleEntity.setBody(articleDTO.getBody());

        return articleMapper.mapToArticleDTO(
                articleRepository.save(articleEntity), userId
        );
    }

    @Override
    @Transactional(readOnly = false)
    public Long deleteArticle(String slug, String username) {
        Optional<ArticleEntity> optionalArticle = articleRepository.findBySlug(slug);

        if (optionalArticle.isEmpty())
            throw new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug));

        if (optionalArticle.get().getAuthor().getEmail().equals(username)) {
            return articleRepository.deleteBySlug(slug);
        }

        throw new ForbiddenException(format(IS_NOT_AN_OWNER_OF_ARTICLE, slug, username));
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDTO.MultipleArticle getArticlesByFilter(ArticleFilter articleFilter, AccountDetails user) {
        Pageable pageable = new OffsetBasedPageRequest(articleFilter.getLimit(), articleFilter.getOffset(), Sort.by(Sort.Order.desc("createdAt")));

        AccountEntity account = null;
        if (articleFilter.getFavorited() != null)
            account = profileService.getAccountByUsername(articleFilter.getFavorited());

        Long userId = null;
        if (user != null)
            userId = user.id();

        return ArticleDTO.MultipleArticle.builder()
                .articles(
                        articleMapper.mapToArticleDTOList(
                                articleRepository.findByFilter(articleFilter.getTag(), articleFilter.getAuthor(), account, pageable),
                                userId)
                ).build();
    }

    @Override
    public ArticleDTO favoriteArticle(String slug, Long userId) {
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

    @Override
    public ArticleDTO unfavoriteArticle(String slug, Long userId) {
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

    @Override
    @Transactional(readOnly = true)
    public ArticleDTO.MultipleArticle getArticlesByFeed(FeedParams feedParams, Long userId) {
        Pageable pageable = new OffsetBasedPageRequest(feedParams.getLimit(), feedParams.getOffset(), Sort.by(Sort.Order.desc("createdAt")));

        AccountEntity user = profileService.getAccountById(userId);

        return ArticleDTO.MultipleArticle.builder()
                .articles(
                        articleMapper.mapToArticleDTOList(
                                articleRepository.findByFeed(user, pageable),
                                userId)
                ).build();
    }
}