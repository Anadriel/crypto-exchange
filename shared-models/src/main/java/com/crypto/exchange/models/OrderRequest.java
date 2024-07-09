package com.crypto.exchange.models;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class OrderRequest implements Serializable {
    @NotNull(message = "User ID cannot be null")
    @Min(value = 1, message = "User ID must be greater than or equal to 1")
    private Long userId;
    @NotNull(message = "Order type cannot be null")
    private OrderType orderType;
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private double amount;
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than zero")
    private double price;
    @NotNull(message = "Base currency cannot be null")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Base currency must have 3 letters in upper case")
    private String baseCurrency;
    @NotNull(message = "Quote currency cannot be null")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Quote currency must have 3 letters in upper case")
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
