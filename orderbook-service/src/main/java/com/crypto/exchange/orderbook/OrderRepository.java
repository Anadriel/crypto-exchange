package com.crypto.exchange.orderbook;

import com.crypto.exchange.models.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.orderType = :orderType AND o.baseCurrency = :baseCurrency " +
           "AND o.quoteCurrency = :quoteCurrency AND o.price = :price AND o.status = 'PLACED'")
    List<Order> findMatchingOrders(
            @Param("orderType") OrderType orderType,
            @Param("baseCurrency") String baseCurrency,
            @Param("quoteCurrency") String quoteCurrency,
            @Param("price") double price
    );
}
