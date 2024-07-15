package com.crypto.exchange.balance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/balances")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/{userId}")
    public List<Balance> getBalances(@PathVariable("userId") Long userId) {
        return balanceService.getBalances(userId);
    }
}
