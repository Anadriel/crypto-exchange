package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.http.OrderRequest;
import com.crypto.exchange.models.OrderStatus;
import com.crypto.exchange.models.Tuple;
import com.crypto.exchange.models.events.OrdersMatched;
import com.crypto.exchange.models.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Queue;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class OrderBookService {

    @Autowired
    private Queue orderQueue;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Order placeOrder(OrderRequest orderRequest) {
        // Save to order book
        Order order = new Order(
                orderRequest.getUserId(),
                orderRequest.getOrderType(),
                orderRequest.getAmount(),
                orderRequest.getPrice(),
                orderRequest.getBaseCurrency(),
                orderRequest.getQuoteCurrency(),
                OrderStatus.PLACED
        );
        orderRepository.save(order);
        log.info("Order with id '{}' was saved", order);

        //Make a copy of newly created order for returning
        Order savedOrder = new Order(order);

        // Try to match the order
        matchOrder(order);

        return savedOrder;
    }

    private void matchOrder(Order order) {
        log.info("Matching order with id '{}' started", order.getId());
        List<Order> matchingOrders = orderRepository.findMatchingOrders(
                order.getOrderType() == OrderType.BUY ? OrderType.SELL : OrderType.BUY,
                order.getBaseCurrency(),
                order.getQuoteCurrency(),
                order.getPrice()
        );

        matchingOrders
                .stream()
                .takeWhile(matchingOrder -> order.getAmount() > 0)
                .forEachOrdered(matchingOrder -> {
                    double matchedAmount = Math.min(order.getAmount(), matchingOrder.getAmount());

                    log.info("Orders '{}' and '{}' are matched with amount '{}' and getting updated in DB",
                            order.getId(), matchingOrder.getId(), matchedAmount);

                    // Update orders
                    updateOrders(order, matchingOrder, matchedAmount);

                    log.info("Event about matching orders '{}' and '{}' is publishing",
                            order.getId(), matchingOrder.getId());

                    // Publish match event to RabbitMQ
                    publishMatchEvent(order, matchingOrder, matchedAmount);
                });

        log.info("Matching order with id '{}' finished", order.getId());
    }

    private void publishMatchEvent(Order currentOrder, Order matchingOrder, double matchedAmount) {
        Tuple<Long, Long> buyerUserIdAndSellerUserId = switch (currentOrder.getOrderType()) {
            case BUY -> new Tuple<>(currentOrder.getUserId(), matchingOrder.getUserId());
            case SELL -> new Tuple<>(matchingOrder.getUserId(), currentOrder.getUserId());
        };
        OrdersMatched ordersMatched = new OrdersMatched(
            buyerUserIdAndSellerUserId.x,
            buyerUserIdAndSellerUserId.y,
            matchedAmount,
            matchingOrder.getPrice(),
            currentOrder.getBaseCurrency(),
            currentOrder.getQuoteCurrency()
        );
        log.info("Sending OrdersMatched event: '{}'", ordersMatched);
        rabbitTemplate.convertAndSend(orderQueue.getName(), ordersMatched);
    }

    private void updateOrderStatusAndAmount(Order order, double matchedAmount) {
        order.setAmount(order.getAmount() - matchedAmount);
        if (order.getAmount() == 0) {
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            order.setStatus(OrderStatus.PARTIAL);
        }
        orderRepository.save(order);
    }

    @Transactional
    private void updateOrders(Order currentOrder, Order matchingOrder, double matchedAmount) {
        updateOrderStatusAndAmount(currentOrder, matchedAmount);
        updateOrderStatusAndAmount(matchingOrder, matchedAmount);
    }
}
