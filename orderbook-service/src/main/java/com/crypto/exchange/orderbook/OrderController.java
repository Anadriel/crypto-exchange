package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderBookService orderBookService;

    @PostMapping
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        orderBookService.placeOrder(orderRequest);
        return "Order placed successfully";
    }
}
