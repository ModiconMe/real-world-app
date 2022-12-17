package edu.popov.domain.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.CommentEntity;
import edu.popov.domain.profile.dto.ProfileDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class CommentDTO {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String body;
    private ProfileDTO author;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    public static class SingleComment {

        private CommentDTO comment;

    }
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    public static class MultipleComments {

        private List<CommentDTO> comments;

    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("comment")
    public static class Create {

        @NotBlank(message = "Body of the comment cannot be blank")
        private String body;

    }

}