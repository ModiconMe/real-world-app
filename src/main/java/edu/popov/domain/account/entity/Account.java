package edu.popov.domain.account.entity;

import edu.popov.domain.profile.entity.FollowRelation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
@ToString
@Getter
@Setter
@Entity(name = "Account")
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(name = "account_email_unique", columnNames = "email"),
                @UniqueConstraint(name = "account_username_unique", columnNames = "user_name")
        }
)
public class Account {

    @Id
    @SequenceGenerator(name = "account_sequence", sequenceName = "account_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "user_name", nullable = false, columnDefinition = "TEXT")
    private String username;
    @Column(name = "email", nullable = false, columnDefinition = "TEXT")
    private String email;
    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    private String password;
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    @Column(name = "image")
    private String image;

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime createdAt;
    @Column(
            name = "updated_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime updatedAt;

    @Singular
    @OneToMany(
            mappedBy = "accountToFollow",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FollowRelation> followers;

    @Singular
    @OneToMany(
            mappedBy = "userAccount",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FollowRelation> followings;

}