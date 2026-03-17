import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFabButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AccountService } from '../../../core/services/account.service';
import { AccountSummary } from '../../../core/models/account.model';
import { IbanFormatPipe } from '../../../shared/pipes/iban-format.pipe';
import { AccountCreateDialogComponent } from '../account-create-dialog/account-create-dialog.component';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DatePipe,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    IbanFormatPipe,
  ],
  templateUrl: './account-list.component.html',
  styleUrl: './account-list.component.scss',
})
export class AccountListComponent implements OnInit {
  private readonly accountService = inject(AccountService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly accounts = signal<AccountSummary[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading.set(true);
    this.accountService.getMyAccounts().subscribe({
      next: (accounts) => {
        this.accounts.set(accounts);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  openCreateDialog(): void {
    const ref = this.dialog.open(AccountCreateDialogComponent, {
      width: '480px',
      maxWidth: '95vw',
      panelClass: 'denario-dialog',
    });

    ref.afterClosed().subscribe(result => {
      if (result) {
        this.snackBar.open('Account created successfully!', 'Close', {
          duration: 3000,
          panelClass: ['snack-success'],
        });
        this.loadAccounts();
      }
    });
  }

  getAccountIcon(type: string): string {
    return type === 'SAVINGS' ? 'savings' : 'account_balance_wallet';
  }
}
