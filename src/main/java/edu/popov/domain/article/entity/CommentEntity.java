package edu.popov.domain.article.entity;

import edu.popov.domain.account.entity.AccountEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
@Getter
@Entity(name = "Comment")
@Table(name = "comment")
public class CommentEntity {

    @Id
    @SequenceGenerator(name = "comment_sequence", sequenceName = "comment_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "account_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "comment_account_id_fk")
    )
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "article_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "comment_article_id_fk")
    )
    private ArticleEntity article;

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

}