package com.tech45degree.bankingservice.service;

import com.tech45degree.bankingservice.constant.TransactionStatus;
import com.tech45degree.bankingservice.model.Transaction;
import com.tech45degree.bankingservice.model.User;
import com.tech45degree.bankingservice.repository.TransactionRepository;
import com.tech45degree.bankingservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    private WebClient webClient;

    public Mono<Transaction> process(Transaction transaction) {

        return Mono.just(transaction)
                .mapNotNull(t ->
                        {
                            if (t.getStatus().equals(TransactionStatus.INITIATED))
                            {
                                transactionRepo.save(t);
                                User user = userRepo.findByCardId(t.getCardId());
                                log.info("User Details {}", user);
                                if (Objects.isNull(user)) {
                                    t.setStatus(TransactionStatus.CARD_INVALID);
                                    transactionRepo.save(t);
                                } else if (user.isAccountLocked()) {
                                    t.setStatus(TransactionStatus.ACCOUNT_BLOCKED);
                                    transactionRepo.save(t);
                                } else {
                                    if (user.getHomeCountry().equalsIgnoreCase(t.getTransactionLocation())) {
                                        t.setStatus(TransactionStatus.VALID);
                                        // Call Reporting Service to report valid transaction
                                        // to bank and deduct amount if funds available
                                        return webClient.post()
                                                .uri(REPORTING_SERVICE_URL)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .body(BodyInserters.fromValue(t))
                                                .retrieve()
                                                .bodyToMono(Transaction.class)
                                                .zipWhen(t1 ->
                                                                // Call Account Manager service to process
                                                                // the transaction and send the money
                                                                webClient.post()
                                                                        .uri(ACCOUNT_MANAGER_SERVICE_URL)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .body(BodyInserters.fromValue(t1))
                                                                        .retrieve()
                                                                        .bodyToMono(Transaction.class)
                                                                        .log(),
                                                        (t1, t2) -> t2
                                                )
                                                .log()
                                                .share()
                                                .block();
                                    } else {
                                        t.setStatus(TransactionStatus.FRAUDULENT);

                                        // Call User Notification service to notify
                                        // for a fraudulent transaction
                                        // attempt from the User's card
                                        return webClient.post()
                                                .uri(USER_NOTIFICATION_SERVICE_URL)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .body(BodyInserters.fromValue(t))
                                                .retrieve()
                                                .bodyToMono(Transaction.class)
                                                .zipWhen(t1 ->
                                                                // Call Reporting Service to notify bank
                                                                // that there has been an attempt for fraudulent transaction
                                                                // and if this attempt exceeds 3 times then auto-block
                                                                // the card and account
                                                                webClient.post()
                                                                        .uri(REPORTING_SERVICE_URL)
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .body(BodyInserters.fromValue(t1))
                                                                        .retrieve()
                                                                        .bodyToMono(Transaction.class)
                                                                        .log(),
                                                        (t1, t2) -> t2
                                                )
                                                .log()
                                                .share()
                                                .block();
                                    }
                                }
                            } else {
                                t.setStatus(TransactionStatus.FAILURE);
                            }
                            return t;
                        });
    }
}