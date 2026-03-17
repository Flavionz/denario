import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AccountService } from '../../core/services/account.service';
import { TransactionService } from '../../core/services/transaction.service';
import { AccountSummary } from '../../core/models/account.model';
import { TransactionResponse } from '../../core/models/transaction.model';
import { IbanFormatPipe } from '../../shared/pipes/iban-format.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    CurrencyPipe,
    DatePipe,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    IbanFormatPipe,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly accountService = inject(AccountService);
  private readonly transactionService = inject(TransactionService);

  readonly accounts = signal<AccountSummary[]>([]);
  readonly recentTransactions = signal<TransactionResponse[]>([]);
  readonly loadingAccounts = signal(true);
  readonly loadingTransactions = signal(true);

  readonly totalBalance = computed(() =>
    this.accounts().reduce((sum, a) => sum + a.balance, 0)
  );

  readonly totalAccountsCount = computed(() => this.accounts().length);

  readonly lastActivity = computed(() => {
    const txns = this.recentTransactions();
    if (!txns.length) return null;
    return txns[0].createdAt;
  });

  readonly transactionColumns = ['date', 'description', 'amount', 'status'];

  ngOnInit(): void {
    this.loadAccounts();
    this.loadTransactions();
  }

  private loadAccounts(): void {
    this.accountService.getMyAccounts().subscribe({
      next: (accounts) => {
        this.accounts.set(accounts);
        this.loadingAccounts.set(false);
      },
      error: () => this.loadingAccounts.set(false),
    });
  }

  private loadTransactions(): void {
    this.transactionService.getMyTransactions(0, 5).subscribe({
      next: (paged) => {
        this.recentTransactions.set(paged.content);
        this.loadingTransactions.set(false);
      },
      error: () => this.loadingTransactions.set(false),
    });
  }

  getAccountIcon(type: string): string {
    return type === 'SAVINGS' ? 'savings' : 'account_balance_wallet';
  }

  isIncoming(txn: TransactionResponse, accounts: AccountSummary[]): boolean {
    return accounts.some(a => a.iban === txn.targetIban);
  }
}
