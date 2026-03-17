import { Injectable, inject, OnDestroy } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';
import { WebSocketNotification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class WebSocketService implements OnDestroy {
  private readonly notificationService = inject(NotificationService);
  private client: Client | null = null;
  private connected = false;

  connect(token: string): void {
    if (this.connected) return;

    this.client = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl) as WebSocket,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: environment.production ? () => {} : (msg) => console.debug('[STOMP]', msg),
      reconnectDelay: 5000,
      onConnect: () => {
        this.connected = true;
        this.subscribeToNotifications();
      },
      onDisconnect: () => {
        this.connected = false;
      },
      onStompError: (frame) => {
        console.error('[STOMP] Error:', frame);
      },
    });

    this.client.activate();
  }

  private subscribeToNotifications(): void {
    if (!this.client) return;

    this.client.subscribe('/user/queue/notifications', (message: IMessage) => {
      try {
        const notification = JSON.parse(message.body) as WebSocketNotification;
        this.notificationService.addRealtime(notification);
      } catch (e) {
        console.error('[WebSocket] Failed to parse notification:', e);
      }
    });
  }

  disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
      this.connected = false;
    }
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
