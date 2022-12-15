package edu.popov.domain.account.repository;

import edu.popov.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    @Query("SELECT a FROM Account a WHERE a.username = :username")
    Optional<Account> findByUsername(@Param("username")String username);

    @Query("SELECT a FROM Account a WHERE a.email = :email AND a.password = :password")
    Optional<Account> findByEmailAndPassword(@Param("email")String email, @Param("password")String password);
}
