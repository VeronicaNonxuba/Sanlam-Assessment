package com.Sanlam.Banking;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

@Getter
public record WithdrawalEvent(BigDecimal amount, Long accountId, String status) {

    // Convert to JSON String using Jackson library
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("amount", amount.toString());
        rootNode.put("accountId", accountId);
        rootNode.put("status", status);
        return rootNode.toString();
    }
}