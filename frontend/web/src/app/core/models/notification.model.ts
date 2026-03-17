export type NotificationType = 'TRANSFER_SENT' | 'TRANSFER_RECEIVED' | 'ACCOUNT_CREATED' | 'ACCOUNT_FROZEN' | 'BALANCE_LOW' | 'SYSTEM';

export interface NotificationResponse {
  id: string;
  userId: string;
  type: NotificationType;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
  metadata?: Record<string, string>;
}

export interface PagedNotificationsResponse {
  content: NotificationResponse[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface WebSocketNotification {
  type: NotificationType;
  title: string;
  message: string;
  timestamp: string;
  metadata?: Record<string, string>;
}
