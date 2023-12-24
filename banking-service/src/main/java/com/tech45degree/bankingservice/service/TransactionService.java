package com.tech45degree.bankingservice.service;

import com.tech45degree.bankingservice.constant.TransactionStatus;
import com.tech45degree.bankingservice.model.Transaction;
import com.tech45degree.bankingservice.model.User;
import com.tech45degree.bankingservice.repository.TransactionRepository;
import com.tech45degree.bankingservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Service
public class TransactionService {

    private static final String USER_NOTIFICATION_SERVICE_URL =
            "http://user-notification-service:8081/notify/fraudulent-transaction";
    private static final String REPORTING_SERVICE_URL =
            "http://reporting-service:8082/report/";
    private static final String ACCOUNT_MANAGER_SERVICE_URL =
            "http://account-management-service:8083/banking/process";

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RestTemplate restTemplate;

    public Transaction process(Transaction transaction) {

        Transaction firstProcessed;
        Transaction secondProcessed = null;
        transactionRepo.save(transaction);
        if (transaction.getStatus().equals(TransactionStatus.INITIATED)) {

            User user = userRepo.findByCardId(transaction.getCardId());

            // Check whether the card details are valid or not
            if (Objects.isNull(user)) {
                transaction.setStatus(TransactionStatus.CARD_INVALID);
            }

            // Check whether the account is blocked or not
            else if (user.isAccountLocked()) {
                transaction.setStatus(TransactionStatus.ACCOUNT_BLOCKED);
            }

            else {

                // Check if it's a valid transaction or not. The Transaction
                // would be considered valid if it has been requested from
                // the same home country of the user, else will be considered
                // as fraudulent
                if (user.getHomeCountry().equalsIgnoreCase(transaction
                        .getTransactionLocation())) {

                    transaction.setStatus(TransactionStatus.VALID);

                    // Call Reporting Service to report valid transaction to bank
                    // and deduct amount if funds available
                    firstProcessed = restTemplate.postForObject(REPORTING_SERVICE_URL,
                            transaction,
                            Transaction.class);

                    // Call Account Manager service to process the transaction
                    // and send the money
                    if (Objects.nonNull(firstProcessed)) {
                        secondProcessed = restTemplate.postForObject(ACCOUNT_MANAGER_SERVICE_URL,
                                firstProcessed,
                                Transaction.class);
                    }

                    if (Objects.nonNull(secondProcessed)) {
                        transaction = secondProcessed;
                    }
                } else {

                    transaction.setStatus(TransactionStatus.FRAUDULENT);

                    // Call User Notification service to notify for a
                    // fraudulent transaction attempt from the User's card
                    firstProcessed = restTemplate.postForObject(USER_NOTIFICATION_SERVICE_URL,
                            transaction,
                            Transaction.class);

                    // Call Reporting Service to notify bank that
                    // there has been an attempt for fraudulent transaction
                    // and if this attempt exceeds 3 times then auto-block
                    // the card and account
                    if (Objects.nonNull(firstProcessed)) {
                        secondProcessed = restTemplate.postForObject(REPORTING_SERVICE_URL,
                                firstProcessed,
                                Transaction.class);
                    }

                    if (Objects.nonNull(secondProcessed)) {
                        transaction = secondProcessed;
                    }
                }
            }
        } else {

            // For any other case, the transaction will be considered failure
            transaction.setStatus(TransactionStatus.FAILURE);
        }
        return transactionRepo.save(transaction);
    }
}