package com.tech45degree.accountmanagementservice.repository;

import com.tech45degree.accountmanagementservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

   Transaction save(Transaction transaction);
}
