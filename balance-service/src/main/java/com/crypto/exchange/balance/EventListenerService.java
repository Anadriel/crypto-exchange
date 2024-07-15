package com.crypto.exchange.balance;

import com.crypto.exchange.models.events.OrdersMatched;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventListenerService {

    @Autowired
    private Queue queue;

    @Autowired
    private BalanceService balanceService;

    @RabbitListener(queues = "#{orderQueue.name}")
    public void receiveMatchEvent(OrdersMatched ordersMatched) {
        log.info("Received: '{}'", ordersMatched);
        try {
            balanceService.updateBalances(ordersMatched);
            // TODO: Implement more specific error handler
        } catch (Exception e) {
            log.error("OrdersMatched event handling failed with: '{}'", e.getMessage());
            // TODO: Implement undelivered messages handling (separate db or queue)
        }
    }
}
