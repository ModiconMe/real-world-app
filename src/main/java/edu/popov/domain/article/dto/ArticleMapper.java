package edu.popov.domain.article.dto;

import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.FavoriteEntity;
import edu.popov.domain.profile.dto.ProfileMapper;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.domain.tag.entity.TagEntity;
import edu.popov.security.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleMapper {

    private final ProfileMapper profileMapper;
    private final ProfileService profileService;

    public ArticleDTO mapToArticleDTO(ArticleEntity article, Long userId) {
        List<FavoriteEntity> favoriteList = article.getFavoriteList();
        return ArticleDTO.builder()
                .slug(article.getSlug())
                .title(article.getTitle())
                .description(article.getDescription())
                .body(article.getBody())
                .author(profileService.getProfile(article.getAuthor().getUsername(), userId))
                .tags(article.getTags().stream().map(TagEntity::getTagName).toList())
                .updatedAt(article.getUpdatedAt())
                .createdAt(article.getCreatedAt())
                .favorited(favoriteList.stream().anyMatch(favoriteEntity -> favoriteEntity.getAccount().getId().equals(userId)))
                .favoritesCount((long) favoriteList.size())
                .build();
    }

    public List<ArticleDTO> mapToArticleDTOList(List<ArticleEntity> articleEntities, Long accountDetailsId) {
        return articleEntities.stream().map(entity ->
                mapToArticleDTO(entity, accountDetailsId)
        ).toList();
    }
}