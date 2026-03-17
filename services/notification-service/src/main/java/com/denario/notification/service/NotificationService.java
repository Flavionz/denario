package com.denario.notification.service;

import com.denario.notification.dto.NotificationDto.*;
import com.denario.notification.model.Notification;
import com.denario.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification createAndSend(String userId, Notification.NotificationType type,
                                      String title, String message, String referenceId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .build();

        notification = notificationRepository.save(notification);

        try {
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/notifications",
                    WebSocketNotification.from(notification)
            );
            log.info("WebSocket notification sent to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to user {}: {}", userId, e.getMessage());
        }

        return notification;
    }

    public PagedNotificationsResponse getMyNotifications(String userId, int page, int size) {
        Page<Notification> result = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));

        long unreadCount = notificationRepository.countByUserIdAndReadFalse(userId);

        return PagedNotificationsResponse.builder()
                .notifications(result.getContent().stream().map(NotificationResponse::from).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .unreadCount(unreadCount)
                .build();
    }

    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        log.info("Marked all notifications as read for user: {}", userId);
    }
}
