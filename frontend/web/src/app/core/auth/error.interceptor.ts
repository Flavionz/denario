import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from './auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let message = 'An unexpected error occurred';

      switch (error.status) {
        case 401:
          message = 'Session expired. Please log in again.';
          snackBar.open(message, 'Close', {
            duration: 4000,
            panelClass: ['snack-error'],
          });
          authService.login();
          break;
        case 403:
          message = 'Access denied. You do not have permission.';
          snackBar.open(message, 'Close', {
            duration: 4000,
            panelClass: ['snack-error'],
          });
          break;
        case 404:
          message = 'The requested resource was not found.';
          snackBar.open(message, 'Close', {
            duration: 3000,
            panelClass: ['snack-error'],
          });
          break;
        case 503:
          message = 'Service temporarily unavailable. Please try again later.';
          snackBar.open(message, 'Close', {
            duration: 5000,
            panelClass: ['snack-error'],
          });
          break;
        default:
          if (error.status >= 400) {
            message = error.error?.message ?? error.message ?? message;
            snackBar.open(message, 'Close', {
              duration: 4000,
              panelClass: ['snack-error'],
            });
          }
      }

      return throwError(() => error);
    })
  );
};
