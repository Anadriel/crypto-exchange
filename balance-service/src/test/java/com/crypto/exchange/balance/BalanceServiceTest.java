package com.crypto.exchange.balance;

import com.crypto.exchange.models.events.OrdersMatched;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
public class BalanceServiceTest {

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @Test
    public void testUpdateBalances() {
        MockitoAnnotations.openMocks(this);

        OrdersMatched ordersMatched =
            new OrdersMatched(1L, 2L, 10, 100, "BTC", "USD");

        Balance user1BaseBalance = new Balance(1L, "BTC", 100D, 0D);
        Balance user1QuoteBalance = new Balance(1L, "USD", 10000D, 0D);

        Balance user2BaseBalance = new Balance(2L, "BTC", 100D, 0D);
        Balance user2QuoteBalance = new Balance(2L, "USD", 10000D, 0D);

        when(balanceRepository.findByUserIdAndCurrency(1L, "BTC")).thenReturn(user1BaseBalance);
        when(balanceRepository.findByUserIdAndCurrency(1L, "USD")).thenReturn(user1QuoteBalance);
        when(balanceRepository.findByUserIdAndCurrency(2L, "BTC")).thenReturn(user2BaseBalance);
        when(balanceRepository.findByUserIdAndCurrency(2L, "USD")).thenReturn(user2QuoteBalance);

        balanceService.updateBalances(ordersMatched);

        verify(balanceRepository, times(1)).save(user1BaseBalance);
        verify(balanceRepository, times(1)).save(user1QuoteBalance);
        verify(balanceRepository, times(1)).save(user2BaseBalance);
        verify(balanceRepository, times(1)).save(user2QuoteBalance);

    }
}
