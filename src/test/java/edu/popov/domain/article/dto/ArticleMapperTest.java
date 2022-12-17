package edu.popov.domain.article.dto;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.FavoriteEntity;
import edu.popov.domain.article.entity.FavoriteEntityId;
import edu.popov.domain.profile.dto.ProfileDTO;
import edu.popov.domain.profile.dto.ProfileMapper;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.domain.tag.entity.TagEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleMapperTest {

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private ProfileService profileService;

    private ArticleMapper articleMapper;

    @BeforeEach
    void setUp() {
        articleMapper = new ArticleMapper(profileMapper, profileService);
    }

    @Test
    void itShouldMapToDTO() {
        // given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .createdAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .updatedAt(LocalDateTime.of(2022, 12, 11, 17, 20, 20))
                .build();
        ProfileDTO profileDTO = ProfileDTO.builder()
                .username("user1")
                .build();

        ArticleEntity articleEntity = ArticleEntity.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(account)
                .createdAt(LocalDateTime.of(2022, 11, 10, 12, 12, 12))
                .updatedAt(LocalDateTime.of(2022, 11, 10, 12, 12, 12))
                .build();

        TagEntity tag1 = TagEntity.builder()
                .tagName("tag1")
                .build();
        TagEntity tag2 = TagEntity.builder()
                .tagName("tag2")
                .build();
        articleEntity.addTag(tag1);
        articleEntity.addTag(tag2);

        FavoriteEntityId favoriteEntityId1 = FavoriteEntityId.builder()
                .articleId(1L)
                .accountId(1L)
                .build();
        FavoriteEntity favoriteEntity1 = FavoriteEntity.builder()
                .id(favoriteEntityId1)
                .article(articleEntity)
                .account(account)
                .build();
        FavoriteEntityId favoriteEntityId2 = FavoriteEntityId.builder()
                .articleId(1L)
                .accountId(2L)
                .build();
        FavoriteEntity favoriteEntity2 = FavoriteEntity.builder()
                .id(favoriteEntityId1)
                .article(articleEntity)
                .account(account)
                .build();
        articleEntity.addFavorite(favoriteEntity1);
        articleEntity.addFavorite(favoriteEntity2);

        when(profileService.getProfile("user1", 1L)).thenReturn(profileDTO);

        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .author(profileDTO)
                .favoritesCount(2L)
                .favorited(true)
                .tags(List.of("tag1", "tag2"))
                .createdAt(LocalDateTime.of(2022, 11, 10, 12, 12, 12))
                .updatedAt(LocalDateTime.of(2022, 11, 10, 12, 12, 12))
                .build();
        // when
        ArticleDTO expected = articleMapper.mapToArticleDTO(articleEntity, 1L);

        // then
        assertThat(articleDTO).isEqualTo(expected);
    }
}