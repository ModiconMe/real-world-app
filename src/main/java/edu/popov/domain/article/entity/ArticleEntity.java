package edu.popov.domain.article.entity;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.tag.entity.TagEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
@Getter
@Setter
@Entity(name = "Article")
@Table(
        name = "article",
        uniqueConstraints = {
                @UniqueConstraint(name = "article_slug_unique", columnNames = "slug")
        }
)
public class ArticleEntity {

    @Id
    @SequenceGenerator(name = "article_sequence", sequenceName = "article_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "slug", nullable = false, columnDefinition = "TEXT")
    private String slug;
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(
            name = "author_id", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "article_id_account_id_fk")
    )
    private AccountEntity author;

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<TagEntity> tags = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<FavoriteEntity> favoriteList = new ArrayList<>();

    @Column(
            name = "created_at",
            nullable = false
    )
    private ZonedDateTime createdAt;
    @Column(
            name = "updated_at",
            nullable = false
    )
    private ZonedDateTime updatedAt;

    public void addTag(TagEntity tag) {
        tags.add(tag);
        tag.setArticle(this);
    }

    public void addFavorite(FavoriteEntity favorite) {
        favoriteList.add(favorite);
        favorite.setArticle(this);
    }
}