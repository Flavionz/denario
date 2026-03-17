package com.denario.transaction.repository;

import com.denario.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.initiatedBy = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findByInitiatedBy(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.sourceIban = :iban OR t.targetIban = :iban ORDER BY t.createdAt DESC")
    Page<Transaction> findByIban(@Param("iban") String iban, Pageable pageable);

    boolean existsByReference(String reference);
}
