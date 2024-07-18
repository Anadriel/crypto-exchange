package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.OrderStatus;
import com.crypto.exchange.models.OrderType;
import com.crypto.exchange.models.http.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class OrderTransactionalService {

    @Autowired
    private OrderRepository orderRepository;

    Order createOrder(OrderRequest orderRequest) {
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
        log.info("'{}' was saved", order);

        return order;
    }

    List<Order> fetchMatchingOrders(Order order) {
        return orderRepository.findMatchingOrders(
                order.getOrderType() == OrderType.BUY ? OrderType.SELL : OrderType.BUY,
                order.getBaseCurrency(),
                order.getQuoteCurrency(),
                order.getPrice()
        );
    }

    @Transactional
    public void updateOrders(Order currentOrder, Order matchingOrder, double matchedAmount) {
        updateOrderStatusAndAmount(currentOrder, matchedAmount);
        updateOrderStatusAndAmount(matchingOrder, matchedAmount);
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

}
