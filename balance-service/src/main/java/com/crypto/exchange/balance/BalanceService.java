package com.crypto.exchange.balance;

import com.crypto.exchange.models.events.OrdersMatched;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BalanceService {

    @Autowired
    private Queue queue;

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

    public void updateUserCurrencyBalance(Long userId, String currency, Double amountChange) {
        log.info("Finding balance in '{}' for user '{}'", currency, userId);
        Balance balance = balanceRepository.findByUserIdAndCurrency(userId, currency);
        double newBalance = balance.getAmount() + amountChange;
        if (newBalance < 0) {
            throw new RuntimeException("System entered undetermined state because: " +
                    "user balance becomes negative after balance adjustment");
        }
        log.info("Updating balance in '{}' of user '{}' for '{}' units", currency, userId, amountChange);
        balance.setAmount(newBalance);
        balanceRepository.save(balance);
        log.info("Balance in '{}' of user '{}' for '{}' units was updated", currency, userId, amountChange);
    }

    @RabbitListener(queues = "#{orderQueue.name}")
    public void receiveMatchEvent(OrdersMatched ordersMatched) {
        log.info("Received: '{}'", ordersMatched);
        try {
            updateBalances(ordersMatched);
            //TODO: Implement more specific error handler
        } catch (Exception e) {
            log.error("OrdersMatched event handling failed with: '{}'", e.getMessage());
            //TODO: Implement undelivered messages handling (separate db or queue)
        }
    }

    public Balance getBalance(Long userId) {
        // Fetch balance logic for the user
        return balanceRepository.findByUserId(userId);
    }
}
