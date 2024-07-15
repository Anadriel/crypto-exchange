package com.crypto.exchange.balance;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String currency;
    @Setter
    private Double amount;

    //Constructors

    public Balance() {
    }

    public Balance(Long userId, String currency, Double amount) {
        this.userId = userId;
        this.currency = currency;
        this.amount = amount;
    }
}

