package edu.popov.domain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FollowRelationId implements Serializable {

    @Column(name = "follower_id")
    private Long followerId;

    @Column(name = "account_id")
    private Long accountId;

}
