package com.crypto.exchange.balance;

import com.crypto.exchange.models.http.BalanceRequest;
import com.crypto.exchange.models.http.ErrorResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/balances")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/{userId}")
    public List<Balance> getBalances(@PathVariable("userId") Long userId) {
        return balanceService.getBalances(userId);
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody BalanceRequest balanceRequest, BindingResult bindingResult) {
        log.info("Deposit request was gotten: '{}'", balanceRequest);
        return updateBalance(
                balanceRequest.getUserId(),
                balanceRequest.getCurrency(),
                balanceRequest.getAmount(),
                0,
                bindingResult
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody BalanceRequest balanceRequest, BindingResult bindingResult) {
        log.info("Withdraw request was gotten: '{}'", balanceRequest);
        return updateBalance(
                balanceRequest.getUserId(),
                balanceRequest.getCurrency(),
                -balanceRequest.getAmount(),
                0,
                bindingResult
        );
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@Valid @RequestBody BalanceRequest balanceRequest, BindingResult bindingResult) {
        log.info("Reserve request was gotten: '{}'", balanceRequest);
        return updateBalance(
                balanceRequest.getUserId(),
                balanceRequest.getCurrency(),
                -balanceRequest.getAmount(),
                balanceRequest.getAmount(),
                bindingResult
        );
    }

    @PostMapping("/release")
    public ResponseEntity<?> release(@Valid @RequestBody BalanceRequest balanceRequest, BindingResult bindingResult) {
        log.info("Release request was gotten: '{}'", balanceRequest);
        return updateBalance(
                balanceRequest.getUserId(),
                balanceRequest.getCurrency(),
                balanceRequest.getAmount(),
                -balanceRequest.getAmount(),
                bindingResult
        );
    }

    public ResponseEntity<?> updateBalance(long userId, String currency,
                                           double amount, double reservedAmount, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> messages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), messages);
            return errorResponse.toResponseEntity();
        }

        try {
            Balance balance = balanceService.updateUserCurrencyBalance(userId, currency, amount, reservedAmount);
            log.info("Balance was updated: '{}'", balance);
            return ResponseEntity.status(HttpStatus.OK).body(balance);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Balance updating failed with: '{}'", errorMessage);
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
            return errorResponse.toResponseEntity();
        }
    }
}
