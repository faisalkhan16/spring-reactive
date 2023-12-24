package com.tech45degree.accountmanagementservice.controller;

import com.tech45degree.accountmanagementservice.constant.TransactionStatus;
import com.tech45degree.accountmanagementservice.model.Transaction;
import com.tech45degree.accountmanagementservice.service.AccountManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/banking")
public class AccountManagementController {

    @Autowired
    private AccountManagementService accountManagementService;

    @PostMapping("/process")
    public ResponseEntity<Transaction> manage(@RequestBody Transaction transaction) {
        log.info("Process transaction with details: {}", transaction);
        Transaction processed = accountManagementService.manage(transaction);
        if (processed.getStatus().equals(TransactionStatus.SUCCESS)) {
            return ResponseEntity.ok(processed);
        } else {
            return ResponseEntity.internalServerError().body(processed);
        }
    }
}