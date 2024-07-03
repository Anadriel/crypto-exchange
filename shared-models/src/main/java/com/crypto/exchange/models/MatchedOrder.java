package com.crypto.exchange.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
public class MatchedOrder implements Serializable {
    private Long id;
    private Long userId;
    private OrderType orderType; // BUY or SELL

    //Constructors

    public MatchedOrder() {
    }

    public MatchedOrder(Long id, Long userId, OrderType orderType) {
        this.id = id;
        this.userId = userId;
        this.orderType = orderType;
    }
}
