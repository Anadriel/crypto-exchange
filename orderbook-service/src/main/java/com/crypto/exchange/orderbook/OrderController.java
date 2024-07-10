package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.ErrorResponse;
import com.crypto.exchange.models.OrderRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderBookService orderBookService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            HttpStatusCode statusCode = HttpStatus.BAD_REQUEST;
            List<String> messages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            ErrorResponse errorResponse = new ErrorResponse(statusCode.value(), messages);
            return ResponseEntity.status(statusCode).body(errorResponse);
        }

        Order order = orderBookService.placeOrder(orderRequest);
        log.info("New order has been placed: '{}'", order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
