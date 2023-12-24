package com.tech45degree.notificationservice.controller;

import com.tech45degree.notificationservice.constant.TransactionStatus;
import com.tech45degree.notificationservice.model.Transaction;
import com.tech45degree.notificationservice.service.UserNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/notify")
public class NotificationController {

    @Autowired
    private UserNotificationService userNotificationService;

    @PostMapping("/fraudulent-transaction")
    public ResponseEntity<Transaction> notify(@RequestBody Transaction transaction) {
        log.info("Process transaction with details and notify user: {}", transaction);
        Transaction processed = userNotificationService.notify(transaction);
        return ResponseEntity.ok(processed);
    }
}
