package com.crypto.exchange.models.events;

import com.crypto.exchange.models.MatchedOrder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter
public class OrdersMatched implements Serializable {
    private double matchedAmount;
    private double price;
    private String baseCurrency;
    private String quoteCurrency;
    private List<MatchedOrder> nestedOrders;

    //Constructors

    public OrdersMatched() {
    }

    public OrdersMatched(
        double matchedAmount,
        double price,
        String baseCurrency,
        String quoteCurrency,
        List<MatchedOrder> nestedOrders
    ) {
        this.matchedAmount = matchedAmount;
        this.price = price;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.nestedOrders = nestedOrders;
    }

}
