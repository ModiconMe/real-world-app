package edu.popov.domain.article.service;

import edu.popov.domain.article.dto.ArticleDTO;
import edu.popov.domain.article.model.ArticleFilter;
import edu.popov.domain.article.model.FeedParams;
import edu.popov.security.AccountDetails;

public interface ArticleService {

    ArticleDTO.MultipleArticle getArticlesByFilter(ArticleFilter articleFilter, AccountDetails user);

    ArticleDTO.SingleArticle<ArticleDTO> createArticle(ArticleDTO articleDTO, Long userId);

    ArticleDTO.SingleArticle<ArticleDTO> getArticleBySlug(String slug, Long userId);

    ArticleDTO.SingleArticle<ArticleDTO> updateArticle(String slug, ArticleDTO.Update articleDTO, Long userId);

    void deleteArticle(String slug, String username);

    ArticleDTO.SingleArticle<ArticleDTO> favoriteArticle(String slug, Long id);

    ArticleDTO.SingleArticle<ArticleDTO> unfavoriteArticle(String slug, Long id);

    ArticleDTO.MultipleArticle getArticlesByFeed(FeedParams feedParams, Long id);

}