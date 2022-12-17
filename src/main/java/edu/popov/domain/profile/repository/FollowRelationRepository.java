package edu.popov.domain.profile.repository;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.profile.entity.FollowRelationEntity;
import edu.popov.domain.profile.entity.FollowRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRelationRepository extends JpaRepository<FollowRelationEntity, FollowRelationId> {

    @Query("SELECT fr FROM FollowRelation fr WHERE fr.accountToFollow = :accountToFollow AND fr.userAccount = :userAccount")
    Optional<FollowRelationEntity> findByAccountToFollowAndUserAccount(AccountEntity accountToFollow, AccountEntity userAccount);

    @Query("SELECT ac FROM FollowRelation fr JOIN Account ac ON fr.userAccount = ac.id WHERE fr.accountToFollow = :userAccount")
    List<AccountEntity> findFollowers(@Param("userAccount") AccountEntity userAccount);

    @Query("SELECT ac FROM FollowRelation fr JOIN Account ac ON fr.accountToFollow = ac.id WHERE fr.userAccount = :userAccount")
    List<AccountEntity> findFollowings(@Param("userAccount") AccountEntity userAccount);

}
