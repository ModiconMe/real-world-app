package edu.popov.domain.profile.entity;

import edu.popov.domain.account.entity.AccountEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
@Entity(name = "FollowRelation")
@Table(name = "follow_relation")
public class FollowRelationEntity {

    @EmbeddedId
    private FollowRelationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountToFollowId")
    @JoinColumn(
            name = "account_to_follow_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "relation_account_to_follow_id_fk"
            )
    )
    private AccountEntity accountToFollow;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userAccountId")
    @JoinColumn(
            name = "user_account_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "relation_user_account_id_fk"
            )
    )
    private AccountEntity userAccount;

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime createdAt;
}
