package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.http.OrderRequest;
import com.crypto.exchange.models.OrderType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Collections;

import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderBookServiceTest {

    @InjectMocks
    private OrderBookService orderBookService;

    @Mock
    private OrderRepository orderRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testPlaceOrder() {
        MockitoAnnotations.openMocks(this);
        OrderRequest orderRequest = new OrderRequest(1L, OrderType.BUY, 10, 100, "BTC", "USD");

        when(orderRepository.findMatchingOrders(any(OrderType.class), anyString(), anyString(), anyDouble()))
                .thenReturn(Collections.emptyList());

        orderBookService.placeOrder(orderRequest);

        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
