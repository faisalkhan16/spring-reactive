package com.tech45degree.reportingservice.controller;

import com.tech45degree.reportingservice.constant.TransactionStatus;
import com.tech45degree.reportingservice.model.Transaction;
import com.tech45degree.reportingservice.service.ReportingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportingController {

    @Autowired
    private ReportingService reportingService;

    @PostMapping("/")
    public ResponseEntity<Transaction> report(@RequestBody Transaction transaction) {
        log.info("Process transaction with details: {}", transaction);
        Transaction processed = reportingService.report(transaction);
        return ResponseEntity.ok(processed);
    }
}