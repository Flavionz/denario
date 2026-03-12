package com.denario.transaction.service;

import com.denario.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${denario.rabbitmq.exchanges.transaction}")
    private String transactionExchange;

    @Value("${denario.rabbitmq.routing-keys.transaction-created}")
    private String transactionCreatedRoutingKey;

    public void publishTransactionCompleted(Transaction transaction) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "TRANSACTION_COMPLETED");
        event.put("transactionId", transaction.getId().toString());
        event.put("sourceIban", transaction.getSourceIban());
        event.put("targetIban", transaction.getTargetIban());
        event.put("amount", transaction.getAmount().toString());
        event.put("currency", transaction.getCurrency());
        event.put("reference", transaction.getReference());
        event.put("initiatedBy", transaction.getInitiatedBy());

        try {
            rabbitTemplate.convertAndSend(transactionExchange, transactionCreatedRoutingKey, event);
            log.info("Published TRANSACTION_COMPLETED event: {}", transaction.getReference());
        } catch (Exception e) {
            log.error("Failed to publish TRANSACTION_COMPLETED event: {}", e.getMessage());
        }
    }
}
