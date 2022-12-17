package edu.popov.domain.article.entity;

import edu.popov.domain.account.entity.AccountEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Entity(name = "Favorite")
@Table(name = "favorite")
public class FavoriteEntity {

    @EmbeddedId
    private FavoriteEntityId id;

    @ManyToOne
    @MapsId("articleId") // FavoriteEntityId class (primary key class)
    @JoinColumn(
            name = "article_id", nullable = false,
            foreignKey = @ForeignKey(
                    name = "favourite_article_id_fk"
            )
    )
    private ArticleEntity article;

    @ManyToOne
    @MapsId("accountId") // FavoriteEntityId class (primary key class)
    @JoinColumn(
            name = "account_id", nullable = false,
            foreignKey = @ForeignKey(
                    name = "favourite_account_id_fk"
            )
    )
    private AccountEntity account;

}