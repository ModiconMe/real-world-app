package edu.popov.domain.article.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.popov.domain.profile.dto.ProfileDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class ArticleDTO {

    private String slug;

    @NotBlank(message = "Title should be not blank")
    private String title;

    @NotBlank(message = "Description should be not blank")
    private String description;

    @NotBlank(message = "Body should be not blank")
    private String body;

    @NotEmpty(message = "Tags should be not empty")
    private List<String> tagList;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @JsonProperty(value = "created_at")
    private ZonedDateTime createdAt;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonProperty(value = "updated_at")
    private ZonedDateTime updatedAt;

    private ProfileDTO author;
    private Boolean favorited;
    private Long favoritesCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleArticle<T> {

        private T article;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultipleArticle {

        private List<ArticleDTO> articles;
        private int articlesCount;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {

//        @NotBlank(message = "Title should be not blank")
        private String title;

//        @NotBlank(message = "Description should be not blank")
        private String description;

//        @NotBlank(message = "Body should be not blank")
        private String body;
    }
}