package edu.popov.domain.article.dto;

import com.fasterxml.jackson.annotation.*;
import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.profile.dto.ProfileDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@JsonRootName("article")
public class ArticleDTO {

    private String slug;

    @NotBlank(message = "Title should be not blank")
    private String title;

    @NotBlank(message = "Description should be not blank")
    private String description;

    @NotBlank(message = "Body should be not blank")
    private String body;

    @NotEmpty(message = "Tags should be not empty")
    private List<String> tags;

    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;
    @JsonProperty(value = "updated_at")
    private LocalDateTime updatedAt;

    private ProfileDTO author;
    private Boolean favorited;
    private Long favoritesCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonRootName("articles")
    public static class MultipleArticle {
        private List<ArticleDTO> articles;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonRootName("article")
    public static class Update {

        @NotBlank(message = "Title should be not blank")
        private String title;

        @NotBlank(message = "Description should be not blank")
        private String description;

        @NotBlank(message = "Body should be not blank")
        private String body;
    }
}