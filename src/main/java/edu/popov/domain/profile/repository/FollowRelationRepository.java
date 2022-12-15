package edu.popov.domain.profile.repository;

import edu.popov.domain.account.entity.Account;
import edu.popov.domain.profile.entity.FollowRelation;
import edu.popov.domain.profile.entity.FollowRelationId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRelationRepository extends CrudRepository<FollowRelation, FollowRelationId> {

    @Query("SELECT fr FROM FollowRelation fr WHERE fr.accountToFollow = :accountToFollow AND fr.userAccount = :userAccount")
    Optional<FollowRelation> findByAccountToFollowAndUserAccount(Account accountToFollow, Account userAccount);

    @Query("SELECT ac FROM FollowRelation fr JOIN Account ac ON fr.userAccount = ac.id WHERE fr.accountToFollow = :userAccount")
    List<Account> findFollowers(@Param("userAccount") Account userAccount);

    @Query("SELECT ac FROM FollowRelation fr JOIN Account ac ON fr.accountToFollow = ac.id WHERE fr.userAccount = :userAccount")
    List<Account> findFollowings(@Param("userAccount") Account userAccount);

}
