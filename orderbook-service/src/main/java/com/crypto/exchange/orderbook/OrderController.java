package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderBookService orderBookService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        orderBookService.placeOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully");
    }
}
