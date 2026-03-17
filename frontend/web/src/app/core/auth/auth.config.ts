import { AuthConfig } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';

export const authConfig: AuthConfig = {
  issuer: environment.keycloak.issuer,
  clientId: environment.keycloak.clientId,
  redirectUri: environment.keycloak.redirectUri,
  scope: environment.keycloak.scope,
  responseType: 'code',
  useSilentRefresh: false,
  sessionChecksEnabled: true,
  showDebugInformation: !environment.production,
  clearHashAfterLogin: false,
  nonceStateSeparator: 'semicolon',
  requireHttps: false,
};
