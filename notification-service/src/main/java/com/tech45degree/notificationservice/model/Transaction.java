package com.tech45degree.notificationservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tech45degree.notificationservice.constant.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
@Entity
public class Transaction {

    @Id
    @JsonProperty("transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long transactionId;
    private String date;

    @JsonProperty("amount_deducted")
    private double amountDeducted;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("card_id")
    private String cardId;

    @JsonProperty("transaction_location")
    private String transactionLocation;

    @Column(columnDefinition = "ENUM('INITIATED', 'SUCCESS', 'FAILURE', 'CANCELLED', 'VALID', 'ACCOUNT_BLOCKED', 'CARD_INVALID', 'FUNDS_UNAVAILABLE', 'FRAUDULENT', 'FRAUDULENT_NOTIFY_SUCCESS', 'FRAUDULENT_NOTIFY_FAILURE')")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}
