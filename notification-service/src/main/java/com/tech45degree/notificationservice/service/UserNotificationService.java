package com.tech45degree.notificationservice.service;

import com.tech45degree.notificationservice.constant.TransactionStatus;
import com.tech45degree.notificationservice.model.Transaction;
import com.tech45degree.notificationservice.repository.TransactionRepository;
import com.tech45degree.notificationservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserNotificationService {

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JavaMailSender emailSender;

    public Mono<Transaction> notify(Transaction transaction)
    {
        return Mono.just(userRepo.findByCardId(transaction.getCardId()))
                .map(u ->
                {
                    if (transaction.getStatus().equals(TransactionStatus.FRAUDULENT)) {

                        // Notify user by sending email
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setFrom("noreply@baeldung.com");
                        message.setTo(u.getEmail());
                        message.setSubject("Fraudulent transaction attempt from your card");
                        message.setText("An attempt has been made to pay "
                                + transaction.getStoreName()
                                + " from card " + transaction.getCardId() + " in the country "
                                + transaction.getTransactionLocation() + "." +
                                " Please report to your bank or block your card.");
                       // emailSender.send(message);
                        transaction.setStatus(TransactionStatus.FRAUDULENT_NOTIFY_SUCCESS);
                    } else {
                        transaction.setStatus(TransactionStatus.FRAUDULENT_NOTIFY_FAILURE);
                    }
                    transactionRepo.save(transaction);
                    return transaction;
                });

    }
}