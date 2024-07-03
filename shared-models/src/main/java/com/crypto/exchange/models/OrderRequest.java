package com.crypto.exchange.models;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class OrderRequest implements Serializable {
    private Long userId;
    private OrderType orderType;
    private double amount;
    private double price;
    private String baseCurrency;
    private String quoteCurrency;

    //Constructors

    public OrderRequest() {
    }

    public OrderRequest(
        Long userId,
        OrderType orderType,
        double amount,
        double price,
        String baseCurrency,
        String quoteCurrency
    ) {
        this.userId = userId;
        this.orderType = orderType;
        this.amount = amount;
        this.price = price;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

}
