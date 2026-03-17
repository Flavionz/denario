import { Component, ViewChild, inject, signal, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';
import { TopbarComponent } from '../../shared/components/topbar/topbar.component';
import { NotificationPanelComponent } from '../../features/notifications/notification-panel/notification-panel.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatSidenavModule,
    SidebarComponent,
    TopbarComponent,
    NotificationPanelComponent,
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss',
})
export class MainLayoutComponent {
  @ViewChild('sidenav') sidenav!: MatSidenav;

  readonly isMobile = signal<boolean>(false);
  readonly notificationPanelOpen = signal<boolean>(false);

  constructor() {
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  onResize(): void {
    this.checkScreenSize();
  }

  private checkScreenSize(): void {
    this.isMobile.set(window.innerWidth < 768);
  }

  get sidenavMode(): 'side' | 'over' {
    return this.isMobile() ? 'over' : 'side';
  }

  get sidenavOpened(): boolean {
    return !this.isMobile();
  }

  toggleMenu(): void {
    if (this.sidenav) {
      this.sidenav.toggle();
    }
  }

  toggleNotifications(): void {
    this.notificationPanelOpen.update(v => !v);
  }

  closeNotifications(): void {
    this.notificationPanelOpen.set(false);
  }
}
