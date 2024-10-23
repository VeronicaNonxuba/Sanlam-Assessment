package com.Sanlam.Banking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import java.math.BigDecimal;

public class BankAccountService implements IBankAccountService {
    private final JdbcTemplate jdbcTemplate;
    private final SnsClient snsClient;

    @Autowired
    public BankAccountService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.snsClient = SnsClient.builder()
                .region(Region.AF_SOUTH_1) // Specify your region
                .build();
    }

    private static final String WITHDRAWAL_SQL = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
    private static final String BALANCE_SQL = "SELECT balance FROM accounts WHERE id = ?";

    public String withdraw(Long accountId, BigDecimal amount) {
        // Input Validations
        if (accountId == null || accountId <= 0) {
            return "Invalid account ID";
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Withdrawal amount must be positive";
        }

        try {
            BigDecimal currentBalance = jdbcTemplate.queryForObject(BALANCE_SQL,
                    new Object[]{accountId}, BigDecimal.class);

            if (currentBalance == null || currentBalance.compareTo(amount) < 0) {
                return "Insufficient funds for withdrawal";
            }

            int rowsAffected = jdbcTemplate.update(WITHDRAWAL_SQL, amount, accountId);
            if (rowsAffected == 0) {
                return "Withdrawal failed";
            }

            // Publish withdrawal event to SNS
            publishWithdrawalEvent(amount, accountId, "SUCCESSFUL");

            return "Withdrawal successful";
        } catch (DataAccessException e) {
            return "Withdrawal failed: " + e.getMessage();
        }
    }

    private void publishWithdrawalEvent(BigDecimal amount, Long accountId, String status) {
        WithdrawalEvent event = new WithdrawalEvent(amount, accountId, status);
        String eventJson = event.toJson(); // Convert event to JSON
        String snsTopicArn = "arn:aws:sns:YOUR_REGION:YOUR_ACCOUNT_ID:YOUR_TOPIC_NAME";

        PublishRequest publishRequest = PublishRequest.builder()
                .message(eventJson)
                .topicArn(snsTopicArn)
                .build();

        snsClient.publish(publishRequest);
    }
}
