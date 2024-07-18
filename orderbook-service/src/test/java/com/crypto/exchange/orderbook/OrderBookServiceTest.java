package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.OrderStatus;
import com.crypto.exchange.models.OrderType;
import com.crypto.exchange.models.http.OrderRequest;
import com.crypto.exchange.models.events.OrdersMatched;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static com.crypto.exchange.models.OrderStatus.PLACED;
import static com.crypto.exchange.models.OrderType.BUY;
import static com.crypto.exchange.models.OrderType.SELL;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class OrderBookServiceTest {

    @InjectMocks
    private OrderBookService orderBookService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderTransactionalService orderTransactionalService;

    @Mock
    private Queue orderQueue;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlaceOrderWithoutMatchingOrders() {
        // Setup
        OrderRequest orderRequest = new OrderRequest(1L, BUY, 10, 100, "BTC", "USD");

        // Mock responses
        when(orderTransactionalService.fetchMatchingOrders(any(Order.class)))
                .thenReturn(Collections.emptyList());

        // Test
        orderBookService.placeOrder(orderRequest);

        // Verify interactions
        verify(rabbitTemplate, times(0)).convertAndSend(anyString(), any(OrdersMatched.class));
    }

    @Test
    public void testPlaceOrderPublishesMatchEvent() {
        // Setup
        OrderRequest orderRequest = new OrderRequest(1L, BUY, 10, 100, "BTC", "USD");
        Order matchingOrder = new Order(2L, SELL, 10, 100, "BTC", "USD", PLACED);

        // Mock responses
        when(orderTransactionalService.fetchMatchingOrders(any(Order.class)))
                .thenReturn(List.of(matchingOrder));

        when(orderQueue.getName()).thenReturn( "queue_name");

        when(orderTransactionalService.createOrder(any(OrderRequest.class))).thenAnswer(invocation -> {
            OrderRequest req = invocation.getArgument(0);
            return new Order(
                    req.getUserId(),
                    req.getOrderType(),
                    req.getAmount(),
                    req.getPrice(),
                    req.getBaseCurrency(),
                    req.getQuoteCurrency(),
                    PLACED
            );
        });

        // Test
        orderBookService.placeOrder(orderRequest);

        // Verify interactions
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(OrdersMatched.class));
    }
}
