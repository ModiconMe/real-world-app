package edu.popov.domain.account.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(exclude = {"id"})
@Entity(name = "FollowRelation")
@Table(name = "follow_relation")
public class FollowRelation {

    @EmbeddedId
    private FollowRelationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(
            name = "follower_id",
            foreignKey = @ForeignKey(
                    name = "relation_follower_id_fk"
            )
    )
    private Account follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(
                    name = "relation_account_id_fk"
            )
    )
    private Account account;

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime createdAt;
}
