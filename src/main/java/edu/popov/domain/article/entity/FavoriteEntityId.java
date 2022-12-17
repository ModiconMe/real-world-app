package edu.popov.domain.article.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class FavoriteEntityId {

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "article_id")
    private Long articleId;

}