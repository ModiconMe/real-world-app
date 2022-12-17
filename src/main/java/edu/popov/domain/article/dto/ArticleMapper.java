package edu.popov.domain.article.dto;

import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.FavoriteEntity;
import edu.popov.domain.profile.dto.ProfileMapper;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.domain.tag.entity.TagEntity;
import edu.popov.security.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArticleMapper {

    private final ProfileMapper profileMapper;
    private final ProfileService profileService;

    public ArticleDTO mapToSingleArticleDTO(ArticleEntity article, Long userId) {
        List<FavoriteEntity> favoriteList = article.getFavoriteList();

        List<String> tags = new ArrayList<>();
        if (Objects.nonNull(article.getTags()))
            tags = article.getTags().stream().map(TagEntity::getTagName).toList();
        return ArticleDTO.builder()
                        .slug(article.getSlug())
                        .title(article.getTitle())
                        .description(article.getDescription())
                        .body(article.getBody())
                        .author(profileService.getProfile(article.getAuthor().getUsername(), userId))
                        .tagList(tags)
                        .updatedAt(article.getUpdatedAt())
                        .createdAt(article.getCreatedAt())
                        .favorited(favoriteList.stream().anyMatch(favoriteEntity -> favoriteEntity.getAccount().getId().equals(userId)))
                        .favoritesCount((long) favoriteList.size())
                        .build();
    }

    public List<ArticleDTO> mapToMultipleArticleDTOList(List<ArticleEntity> articleEntities, Long accountDetailsId) {
        return articleEntities.stream().map(entity ->
                mapToSingleArticleDTO(entity, accountDetailsId)
        ).toList();
    }
}