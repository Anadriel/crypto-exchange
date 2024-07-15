package com.crypto.exchange.models.http;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class BalanceRequest implements Serializable {
    @NotNull(message = "User ID cannot be null")
    @Min(value = 1, message = "User ID must be greater than or equal to 1")
    private Long userId;
    @NotNull(message = "Currency cannot be null")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must have 3 letters in upper case")
    private String currency;
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private double amount;
}
