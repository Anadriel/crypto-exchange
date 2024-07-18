package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.http.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static com.crypto.exchange.models.OrderStatus.PLACED;
import static com.crypto.exchange.models.OrderType.BUY;
import static com.crypto.exchange.models.OrderType.SELL;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class OrderTransactionalServiceTest {

    @InjectMocks
    private OrderTransactionalService orderTransactionalService;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder() {
        // Setup
        OrderRequest orderRequest = new OrderRequest(1L, BUY, 10, 100, "BTC", "USD");

        // Test
        orderTransactionalService.createOrder(orderRequest);

        // Verify interactions
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testFetchMatchingOrders() {
        // Setup
        Order order = new Order(1L, BUY, 10, 100, "BTC", "USD", PLACED);

        // Test
        orderTransactionalService.fetchMatchingOrders(order);

        // Verify interactions
        verify(orderRepository, times(1))
                .findMatchingOrders(SELL, order.getBaseCurrency(), order.getQuoteCurrency(), order.getPrice());
    }

    @Test
    public void testUpdateOrders() {
        // Setup
        Order order1 = new Order(1L, BUY, 15, 100, "BTC", "USD", PLACED);
        Order order2 = new Order(2L, SELL, 10, 100, "BTC", "USD", PLACED);
        double matchedAmount = 10D;
        // Test
        orderTransactionalService.updateOrders(order1, order2, matchedAmount);

        // Verify interactions
        verify(orderRepository, times(2)).save(any(Order.class));
    }
}

