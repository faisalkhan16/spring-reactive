package com.tech45degree.bankingservice.repository;

import com.tech45degree.bankingservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Transaction save(Transaction transaction);
}
