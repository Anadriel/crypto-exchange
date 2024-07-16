package com.crypto.exchange.balance;

import com.crypto.exchange.models.events.OrdersMatched;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Transactional
    public void updateBalances(OrdersMatched ordersMatched) {

        //TODO: Think about OrdersMatched validation here

        double quoteCurrencyAmount = ordersMatched.getMatchedAmount() * ordersMatched.getPrice();

        //Credit amount in base currency to buyer
        updateUserCurrencyBalance(
                ordersMatched.getBuyerUserId(),
                ordersMatched.getBaseCurrency(),
                ordersMatched.getMatchedAmount(),
                0D
        );

        //Charge amount in quote currency from buyer
        updateUserCurrencyBalance(
                ordersMatched.getBuyerUserId(),
                ordersMatched.getQuoteCurrency(),
                -quoteCurrencyAmount,
                0D
        );

        //Credit amount in quote currency to seller
        updateUserCurrencyBalance(
                ordersMatched.getSellerUserId(),
                ordersMatched.getQuoteCurrency(),
                quoteCurrencyAmount,
                0D
        );

        //Charge amount in base currency from seller
        updateUserCurrencyBalance(
                ordersMatched.getSellerUserId(),
                ordersMatched.getBaseCurrency(),
                -ordersMatched.getMatchedAmount(),
                0D
        );
    }

    public Balance updateUserCurrencyBalance(Long userId, String currency,
                                             Double amountChange, Double reservedAmountChange) {
        log.info("Finding balance in '{}' for user '{}'", currency, userId);
        Balance balance = balanceRepository.findByUserIdAndCurrency(userId, currency);

        if (balance == null) {
            log.info("Balance in '{}' for user '{}' was not found. Create a new one with amount 0.0", currency, userId);
            balance = new Balance(userId, currency, 0D, 0D);
        }

        double newBalanceAmount = balance.getAmount() + amountChange;
        double newBalanceReservedAmount = balance.getReservedAmount() + reservedAmountChange;
        if (newBalanceAmount < 0 || newBalanceReservedAmount < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        log.info("Updating balance in '{}' of user '{}': amount for '{}' units and reserved for '{}' units",
                currency, userId, amountChange, reservedAmountChange);
        balance.setAmount(newBalanceAmount);
        balance.setReservedAmount(newBalanceReservedAmount);
        balanceRepository.save(balance);
        log.info("Balance in '{}' of user '{}' was updated: amount for '{}' units and reserved for '{}' units",
                currency, userId, amountChange, reservedAmountChange);
        return balance;
    }

    public List<Balance> getBalances(Long userId) {
        // Fetch all existed balances for the user
        return balanceRepository.findByUserId(userId);
    }
}
