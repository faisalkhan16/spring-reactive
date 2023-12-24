package com.tech45degree.bankingservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@Entity
public class User {

    @Id
    private String id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;
    private String email;
    private String address;

    @JsonProperty("home_country")
    private String homeCountry;
    private String gender;
    private String mobile;

    @JsonProperty("card_id")
    private String cardId;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("account_type")
    private String accountType;

    @JsonProperty("account_locked")
    private boolean accountLocked;

    @JsonProperty("fraudulent_activity_attempt_count")
    private Long fraudulentActivityAttemptCount;

    @JsonProperty("valid_transactions")
    @ManyToMany()
    private List<Transaction> validTransactions;

    @JsonProperty("fraudulent_transactions")
    @ManyToMany()
    private List<Transaction> fraudulentTransactions;
}
