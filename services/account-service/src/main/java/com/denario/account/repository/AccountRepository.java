package com.denario.account.repository;

import com.denario.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {


    List<Account> findByUserId(String userId);


    Optional<Account> findByIban(String iban);


    boolean existsByIban(String iban);


    Optional<Account> findByIdAndUserId(UUID id, String userId);


    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByUserId(@Param("userId") String userId);
}
