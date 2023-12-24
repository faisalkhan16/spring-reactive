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
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/banking")
public class AccountManagementController {

    @Autowired
    private AccountManagementService accountManagementService;

    @PostMapping("/process")
    public Mono<Transaction> manage(@RequestBody Transaction transaction) {
        log.info("Process transaction with details: {}", transaction);
        Mono<Transaction> transactionMono = accountManagementService.manage(transaction);
        return transactionMono;
    }
}