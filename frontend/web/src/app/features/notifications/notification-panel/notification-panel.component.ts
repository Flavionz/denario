import { Component, inject, output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { NotificationService } from '../../../core/services/notification.service';
import { RelativeTimePipe } from '../../../shared/pipes/relative-time.pipe';
import { NotificationType } from '../../../core/models/notification.model';

@Component({
  selector: 'app-notification-panel',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, MatDividerModule, RelativeTimePipe],
  templateUrl: './notification-panel.component.html',
  styleUrl: './notification-panel.component.scss',
})
export class NotificationPanelComponent implements OnInit {
  readonly notificationService = inject(NotificationService);
  readonly close = output<void>();

  ngOnInit(): void {
    this.notificationService.loadNotifications();
  }

  onClose(): void {
    this.close.emit();
  }

  markAllRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => this.notificationService.markAllReadLocally(),
      error: () => this.notificationService.markAllReadLocally(),
    });
  }

  getNotificationIcon(type: NotificationType): string {
    const icons: Record<NotificationType, string> = {
      TRANSFER_SENT: 'send',
      TRANSFER_RECEIVED: 'call_received',
      ACCOUNT_CREATED: 'account_balance',
      ACCOUNT_FROZEN: 'lock',
      BALANCE_LOW: 'warning',
      SYSTEM: 'info',
    };
    return icons[type] ?? 'notifications';
  }
}
