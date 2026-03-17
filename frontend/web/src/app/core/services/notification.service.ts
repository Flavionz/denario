import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationResponse, PagedNotificationsResponse, WebSocketNotification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/notifications`;

  readonly notifications = signal<NotificationResponse[]>([]);
  readonly realtimeNotifications = signal<WebSocketNotification[]>([]);
  readonly unreadCount = computed(() =>
    this.notifications().filter(n => !n.read).length + this.realtimeNotifications().length
  );

  getMyNotifications(page = 0, size = 20): Observable<PagedNotificationsResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedNotificationsResponse>(this.baseUrl, { params });
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/read-all`, {});
  }

  loadNotifications(page = 0, size = 20): void {
    this.getMyNotifications(page, size).subscribe({
      next: (paged) => this.notifications.set(paged.content),
      error: () => { /* silently fail */ }
    });
  }

  addRealtime(notification: WebSocketNotification): void {
    this.realtimeNotifications.update(list => [notification, ...list]);
  }

  clearRealtime(): void {
    this.realtimeNotifications.set([]);
  }

  markAllReadLocally(): void {
    this.notifications.update(list => list.map(n => ({ ...n, read: true })));
    this.clearRealtime();
  }
}
