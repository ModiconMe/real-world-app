package edu.popov.domain.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class FollowRelationId implements Serializable {

    @Column(name = "account_to_follow_id")
    private Long accountToFollowId;

    @Column(name = "user_account_id")
    private Long userAccountId;

}
