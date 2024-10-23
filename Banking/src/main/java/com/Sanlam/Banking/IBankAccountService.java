package com.Sanlam.Banking;

import java.math.BigDecimal;

public interface IBankAccountService {
    String withdraw(Long accountId, BigDecimal amount);
}
