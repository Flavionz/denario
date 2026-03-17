package com.denario.notification.service;

import com.denario.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${denario.rabbitmq.queues.account-created}")
    public void onAccountCreated(Map<String, Object> event) {
        log.info("Received ACCOUNT_CREATED event: {}", event);

        String userId = (String) event.get("userId");
        String iban = (String) event.get("iban");
        String ownerName = (String) event.get("ownerName");

        notificationService.createAndSend(
                userId,
                Notification.NotificationType.ACCOUNT_CREATED,
                "Account Created",
                "Your account " + iban + " has been successfully created, " + ownerName + "!",
                (String) event.get("accountId")
        );
    }

    @RabbitListener(queues = "${denario.rabbitmq.queues.transaction-created}")
    public void onTransactionCompleted(Map<String, Object> event) {
        log.info("Received TRANSACTION_COMPLETED event: {}", event);

        String userId = (String) event.get("initiatedBy");
        String amount = (String) event.get("amount");
        String currency = (String) event.get("currency");
        String targetIban = (String) event.get("targetIban");
        String reference = (String) event.get("reference");

        notificationService.createAndSend(
                userId,
                Notification.NotificationType.TRANSACTION_COMPLETED,
                "Transfer Completed",
                "Transfer of " + amount + " " + currency + " to " + targetIban + " completed. Ref: " + reference,
                reference
        );
    }
}
