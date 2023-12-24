package com.tech45degree.reportingservice.service;

import com.tech45degree.reportingservice.constant.TransactionStatus;
import com.tech45degree.reportingservice.model.Transaction;
import com.tech45degree.reportingservice.repository.TransactionRepository;
import com.tech45degree.reportingservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ReportingService {

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private UserRepository userRepo;

    public Mono<Transaction> report(Transaction transaction) {

        return Mono.just(userRepo.findByCardId(transaction.getCardId()))
                .map(u ->
                {
                    if (transaction.getStatus().equals(TransactionStatus.FRAUDULENT_NOTIFY_SUCCESS)
                            || transaction.getStatus().equals(
                            TransactionStatus.FRAUDULENT_NOTIFY_FAILURE))
                    {
                        u.setFraudulentActivityAttemptCount(
                                u.getFraudulentActivityAttemptCount() + 1);
                        u.setAccountLocked(u.getFraudulentActivityAttemptCount() > 3);
                        u.getFraudulentTransactions().add(transaction);
                        userRepo.save(u);

                        transaction.setStatus(u.isAccountLocked()
                                ? TransactionStatus.ACCOUNT_BLOCKED
                                : TransactionStatus.FAILURE);
                    }
                    transactionRepo.save(transaction);
                    return transaction;
                });
    }
}
