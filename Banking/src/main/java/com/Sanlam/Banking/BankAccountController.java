package com.Sanlam.Banking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bank")
public class BankAccountController {

    private final IBankAccountService bankAccountService;

    @Autowired
    public BankAccountController(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("accountId") Long accountId,
                           @RequestParam("amount") BigDecimal amount) {

        try {
            return bankAccountService.withdraw(accountId, amount);
        } catch (DataAccessException e) {
            return "Withdrawal failed: " + e.getMessage();
        } catch (Exception e) {
            return "An error occurred during withdrawal: " + e.getMessage();
        }
    }
}