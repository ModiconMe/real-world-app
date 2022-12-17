package edu.popov.domain.tag.entity;

import edu.popov.domain.article.entity.ArticleEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Tag")
@Table(name = "tag")
public class TagEntity {

    @Id
    @SequenceGenerator(name = "tag_sequence", sequenceName = "tag_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "tag_name", nullable = false)
    private String tagName;

    @ManyToOne
    @JoinColumn(
            name = "article_id", nullable = false,
            foreignKey = @ForeignKey(
                    name = "tag_article_id_fk"
            )
    )
    private ArticleEntity article;

}