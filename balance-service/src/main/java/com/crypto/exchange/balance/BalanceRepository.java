package com.crypto.exchange.balance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    @Query("SELECT b FROM Balance b WHERE b.userId = :userId AND b.currency = :currency")
    Balance findByUserIdAndCurrency(
            @Param("userId") Long userId,
            @Param("currency") String currency
    );

    @Query("SELECT b FROM Balance b WHERE b.userId = :userId")
    Balance findByUserId(
            @Param("userId") Long userId
    );
}
