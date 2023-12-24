package com.tech45degree.accountmanagementservice.service;

import com.tech45degree.accountmanagementservice.constant.TransactionStatus;
import com.tech45degree.accountmanagementservice.model.Transaction;
import com.tech45degree.accountmanagementservice.model.User;
import com.tech45degree.accountmanagementservice.repository.TransactionRepository;
import com.tech45degree.accountmanagementservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountManagementService {

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private UserRepository userRepo;

    public Transaction manage(Transaction transaction) {
        if (transaction.getStatus().equals(TransactionStatus.VALID)) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepo.save(transaction);

            User user = userRepo.findByCardId(transaction.getCardId());
            user.getValidTransactions().add(transaction);
            userRepo.save(user);
        }
        return transaction;
    }
}
