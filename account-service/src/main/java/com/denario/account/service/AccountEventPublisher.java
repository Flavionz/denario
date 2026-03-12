package com.denario.account.service;

import com.denario.account.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${denario.rabbitmq.exchanges.account}")
    private String accountExchange;

    @Value("${denario.rabbitmq.routing-keys.account-created}")
    private String accountCreatedRoutingKey;

    @Value("${denario.rabbitmq.routing-keys.account-updated}")
    private String accountUpdatedRoutingKey;

    public void publishAccountCreated(Account account) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "ACCOUNT_CREATED");
        event.put("accountId", account.getId().toString());
        event.put("userId", account.getUserId());
        event.put("iban", account.getIban());
        event.put("ownerName", account.getOwnerName());
        event.put("timestamp", LocalDateTime.now().toString());

        try {
            rabbitTemplate.convertAndSend(accountExchange, accountCreatedRoutingKey, event);
            log.info("Published ACCOUNT_CREATED event for account: {}", account.getId());
        } catch (Exception e) {
            log.error("Failed to publish ACCOUNT_CREATED event: {}", e.getMessage());
        }
    }
}