package com.tech45degree.bankingservice.controller;

import com.tech45degree.bankingservice.model.Transaction;
import com.tech45degree.bankingservice.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/banking")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/process")
    public Mono<Transaction> process(@RequestBody Transaction transaction) {
        log.info("Process transaction with details: {}", transaction);
        return transactionService.process(transaction);
    }
}
