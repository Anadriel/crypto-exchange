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
                ordersMatched.getMatchedAmount()
        );

        //Charge amount in quote currency from buyer
        updateUserCurrencyBalance(
                ordersMatched.getBuyerUserId(),
                ordersMatched.getQuoteCurrency(),
                -quoteCurrencyAmount
        );

        //Credit amount in quote currency to seller
        updateUserCurrencyBalance(
                ordersMatched.getSellerUserId(),
                ordersMatched.getQuoteCurrency(),
                quoteCurrencyAmount
        );

        //Charge amount in base currency from seller
        updateUserCurrencyBalance(
                ordersMatched.getSellerUserId(),
                ordersMatched.getBaseCurrency(),
                -ordersMatched.getMatchedAmount()
        );
    }

    public Balance updateUserCurrencyBalance(Long userId, String currency, Double amountChange) {
        log.info("Finding balance in '{}' for user '{}'", currency, userId);
        Balance balance = balanceRepository.findByUserIdAndCurrency(userId, currency);

        if (balance == null) {
            log.info("Balance in '{}' for user '{}' was not found. Create a new one with amount 0.0", currency, userId);
            balance = new Balance(userId, currency, 0.0D);
        }

        double newBalance = balance.getAmount() + amountChange;
        if (newBalance < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        log.info("Updating balance in '{}' of user '{}' for '{}' units", currency, userId, amountChange);
        balance.setAmount(newBalance);
        balanceRepository.save(balance);
        log.info("Balance in '{}' of user '{}' for '{}' units was updated", currency, userId, amountChange);
        return balance;
    }

    public List<Balance> getBalances(Long userId) {
        // Fetch all existed balances for the user
        return balanceRepository.findByUserId(userId);
    }
}
