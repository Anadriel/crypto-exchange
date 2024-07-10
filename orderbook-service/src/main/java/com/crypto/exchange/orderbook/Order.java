package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.MatchedOrder;
import com.crypto.exchange.models.OrderStatus;
import com.crypto.exchange.models.OrderType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private OrderType orderType; // "BUY" or "SELL"
    @Setter
    private double amount;
    private double price;
    private String baseCurrency;
    private String quoteCurrency;
    @Setter
    @Enumerated(EnumType.STRING)
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

    //Create object's copy
    public Order(Order another) {
        this.id = another.id;
        this.userId = another.userId;
        this.orderType = another.orderType;
        this.amount = another.amount;
        this.price = another.price;
        this.baseCurrency = another.baseCurrency;
        this.quoteCurrency = another.quoteCurrency;
        this.status = another.status;
    }

    //Compose MatchedOrder from Order
    public MatchedOrder toMatchedOrder() {
        return new MatchedOrder(id, userId, orderType);
    }
}
