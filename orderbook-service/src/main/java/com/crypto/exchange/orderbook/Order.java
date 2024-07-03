package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.MatchedOrder;
import com.crypto.exchange.models.OrderStatus;
import com.crypto.exchange.models.OrderType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "`order`")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Getter
    private OrderType orderType; // "buy" or "sell"
    @Setter
    @Getter
    private double amount;
    @Getter
    private double price;
    @Getter
    private String baseCurrency;
    @Getter
    private String quoteCurrency;
    @Setter
    private OrderStatus status;

    //Constructors

    public Order() {
    }

    public Order(
        Long userId,
        OrderType orderType,
        double amount,
        double price,
        String baseCurrency,
        String quoteCurrency,
        OrderStatus status
    ) {
        this.userId = userId;
        this.orderType = orderType;
        this.amount = amount;
        this.price = price;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.status = status;
    }

    //Compose MatchedOrder from Order

    public MatchedOrder toMatchedOrder() {
        return new MatchedOrder(id, userId, orderType);
    }
}
