import { Injectable, inject, signal, computed } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './auth.config';

export interface UserProfile {
  sub: string;
  name: string;
  email: string;
  roles: string[];
  preferredUsername?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly oauthService = inject(OAuthService);

  readonly isAuthenticated = signal<boolean>(false);
  readonly userProfile = signal<UserProfile | null>(null);

  async init(): Promise<void> {
    this.oauthService.configure(authConfig);
    this.oauthService.setupAutomaticSilentRefresh();

    try {
      await this.oauthService.loadDiscoveryDocumentAndTryLogin();
    } catch {
      // Discovery may fail if Keycloak is not running; continue gracefully
    }

    this.updateAuthState();

    this.oauthService.events.subscribe(() => {
      this.updateAuthState();
    });
  }

  private updateAuthState(): void {
    const authenticated = this.oauthService.hasValidAccessToken();
    this.isAuthenticated.set(authenticated);

    if (authenticated) {
      const claims = this.oauthService.getIdentityClaims() as Record<string, unknown> | null;
      if (claims) {
        const realmAccess = claims['realm_access'] as { roles?: string[] } | undefined;
        const roles = realmAccess?.roles ?? [];
        this.userProfile.set({
          sub: claims['sub'] as string ?? '',
          name: claims['name'] as string ?? claims['preferred_username'] as string ?? '',
          email: claims['email'] as string ?? '',
          roles,
          preferredUsername: claims['preferred_username'] as string ?? '',
        });
      }
    } else {
      this.userProfile.set(null);
    }
  }

  login(): void {
    this.oauthService.initCodeFlow();
  }

  logout(): void {
    this.oauthService.logOut();
  }

  getAccessToken(): string {
    return this.oauthService.getAccessToken();
  }

  getUserId(): string {
    return this.userProfile()?.sub ?? '';
  }

  getInitials(): string {
    const name = this.userProfile()?.name ?? '';
    return name
      .split(' ')
      .map(n => n[0])
      .slice(0, 2)
      .join('')
      .toUpperCase() || 'U';
  }
}
