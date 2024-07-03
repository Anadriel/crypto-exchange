package com.crypto.exchange.balance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/{userId}")
    public Balance getBalance(@PathVariable Long userId) {
        return balanceService.getBalance(userId);
    }
}
