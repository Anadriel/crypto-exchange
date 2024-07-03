package com.crypto.exchange.balance;

import com.crypto.exchange.models.events.OrdersMatched;
import com.crypto.exchange.models.MatchedOrder;
import com.crypto.exchange.models.Tuple;
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

    private static Tuple<String, Double> getCurrencyAndAmount(MatchedOrder matchedOrder, OrdersMatched ordersMatched, boolean increasing) {
        Tuple<String, Double> quoteCurrencyAndAmount = new Tuple<>(ordersMatched.getQuoteCurrency(), ordersMatched.getMatchedAmount() * ordersMatched.getPrice());
        Tuple<String, Double> baseCurrencyAndAmount = new Tuple<>(ordersMatched.getBaseCurrency(), ordersMatched.getMatchedAmount());
        return switch (matchedOrder.getOrderType()) {
            case SELL -> increasing ? quoteCurrencyAndAmount : baseCurrencyAndAmount;
            case BUY -> increasing ? baseCurrencyAndAmount : quoteCurrencyAndAmount;
        };
    }

    @Transactional
    public void updateBalances(OrdersMatched ordersMatched) {

        for (MatchedOrder matchedOrder : ordersMatched.getNestedOrders()) {

            Tuple<String, Double> decreasingCurrencyAndAmount = getCurrencyAndAmount(matchedOrder, ordersMatched, false);
            Balance decreasingBalance = balanceRepository.findByUserIdAndCurrency(matchedOrder.getUserId(), decreasingCurrencyAndAmount.x);
            if (decreasingBalance != null) {
                decreasingBalance.setAmount(decreasingBalance.getAmount() - decreasingCurrencyAndAmount.y);
                balanceRepository.save(decreasingBalance);
            }

            Tuple<String, Double> increasingCurrencyAndAmount = getCurrencyAndAmount(matchedOrder, ordersMatched, true);
            Balance increasingBalance = balanceRepository.findByUserIdAndCurrency(matchedOrder.getUserId(), increasingCurrencyAndAmount.x);
            if (increasingBalance != null) {
                increasingBalance.setAmount(increasingBalance.getAmount() + increasingCurrencyAndAmount.y);
                balanceRepository.save(increasingBalance);
            }
        }

    }

    @RabbitListener(queues = "#{orderQueue.name}")
    public void receiveMatchEvent(OrdersMatched ordersMatched) {
        log.info("Received: '{}'", ordersMatched);
        updateBalances(ordersMatched);
    }

    public Balance getBalance(Long userId) {
        // Fetch balance logic for the user
        return balanceRepository.findByUserId(userId);
    }
}
