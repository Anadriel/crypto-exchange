package com.crypto.exchange.models.events;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
public class OrdersMatched implements Serializable {
    private Long buyerUserId;
    private Long sellerUserId;
    private double matchedAmount;
    private double price;
    private String baseCurrency;
    private String quoteCurrency;

    //Constructors

    public OrdersMatched() {
    }

    public OrdersMatched(
        Long buyerUserId,
        Long sellerUserId,
        double matchedAmount,
        double price,
        String baseCurrency,
        String quoteCurrency
    ) {
        this.buyerUserId = buyerUserId;
        this.sellerUserId = sellerUserId;
        this.matchedAmount = matchedAmount;
        this.price = price;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }
}
