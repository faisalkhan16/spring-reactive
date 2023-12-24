package com.tech45degree.accountmanagementservice.service;

import com.tech45degree.accountmanagementservice.constant.TransactionStatus;
import com.tech45degree.accountmanagementservice.model.Transaction;
import com.tech45degree.accountmanagementservice.model.User;
import com.tech45degree.accountmanagementservice.repository.TransactionRepository;
import com.tech45degree.accountmanagementservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class AccountManagementService {

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private UserRepository userRepo;

    public Mono<Transaction> manage(Transaction transaction)
    {

        return Mono.just(userRepo.findByCardId(transaction.getCardId()))
                .map(u -> {
                    if (transaction.getStatus().equals(TransactionStatus.VALID))
                    {
                        transaction.setStatus(TransactionStatus.SUCCESS);
                        List<Transaction> transactionList = new ArrayList<>();
                        transactionList.add(transaction);
                        if (Objects.isNull(u.getValidTransactions())
                                || u.getValidTransactions().isEmpty()) {
                            u.setValidTransactions(transactionList);
                        } else {
                            u.getValidTransactions().add(transaction);
                        }
                        log.info("User details: {}", u);
                        userRepo.save(u);
                    }
                    transactionRepo.save(transaction);
                    return transaction;
                });
    }
}
