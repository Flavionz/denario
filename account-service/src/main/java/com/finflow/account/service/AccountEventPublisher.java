package com.finflow.account.service;

import com.finflow.account.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Pubblica eventi su RabbitMQ quando succede qualcosa di importante.
 *
 * PERCHÉ EVENTI ASINCRONI?
 * Quando creiamo un account, vogliamo notificare l'utente via WebSocket.
 * Ma account-service NON conosce notification-service — non lo chiama direttamente.
 * Invece pubblica un evento su RabbitMQ e chiunque è interessato lo ascolta.
 *
 * Questo è il pattern "Event-Driven Architecture" — disaccoppiamento totale.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${finflow.rabbitmq.exchanges.account}")
    private String accountExchange;

    @Value("${finflow.rabbitmq.routing-keys.account-created}")
    private String accountCreatedRoutingKey;

    @Value("${finflow.rabbitmq.routing-keys.account-updated}")
    private String accountUpdatedRoutingKey;

    /**
     * Pubblica evento quando un account viene creato.
     * notification-service riceverà questo messaggio e invierà
     * una notifica WebSocket all'utente.
     */
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
            // Non far fallire la creazione dell'account se RabbitMQ non è disponibile
            log.error("Failed to publish ACCOUNT_CREATED event: {}", e.getMessage());
        }
    }
}
