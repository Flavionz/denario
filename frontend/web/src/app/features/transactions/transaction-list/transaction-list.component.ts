import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatExpansionModule } from '@angular/material/expansion';
import { TransactionService } from '../../../core/services/transaction.service';
import { AccountService } from '../../../core/services/account.service';
import { TransactionResponse, PagedTransactionsResponse } from '../../../core/models/transaction.model';
import { AccountSummary } from '../../../core/models/account.model';
import { IbanFormatPipe } from '../../../shared/pipes/iban-format.pipe';
import { TransferFormComponent } from '../transfer-form/transfer-form.component';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CurrencyPipe,
    DatePipe,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatExpansionModule,
    IbanFormatPipe,
    TransferFormComponent,
  ],
  templateUrl: './transaction-list.component.html',
  styleUrl: './transaction-list.component.scss',
})
export class TransactionListComponent implements OnInit {
  private readonly transactionService = inject(TransactionService);
  private readonly accountService = inject(AccountService);

  readonly transactions = signal<TransactionResponse[]>([]);
  readonly accounts = signal<AccountSummary[]>([]);
  readonly loading = signal(true);
  readonly totalElements = signal(0);
  readonly pageSize = signal(10);
  readonly currentPage = signal(0);
  readonly ibanFilter = signal('');

  readonly displayedColumns = ['date', 'description', 'sourceIban', 'targetIban', 'amount', 'status'];

  ngOnInit(): void {
    this.loadAccounts();
    this.loadTransactions();
  }

  loadAccounts(): void {
    this.accountService.getMyAccounts().subscribe({
      next: (a) => this.accounts.set(a),
    });
  }

  loadTransactions(): void {
    this.loading.set(true);
    const filter = this.ibanFilter().trim();

    const obs = filter
      ? this.transactionService.getTransactionsByIban(filter, this.currentPage(), this.pageSize())
      : this.transactionService.getMyTransactions(this.currentPage(), this.pageSize());

    obs.subscribe({
      next: (paged) => {
        this.transactions.set(paged.content);
        this.totalElements.set(paged.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadTransactions();
  }

  applyFilter(): void {
    this.currentPage.set(0);
    this.loadTransactions();
  }

  clearFilter(): void {
    this.ibanFilter.set('');
    this.currentPage.set(0);
    this.loadTransactions();
  }

  onTransferred(): void {
    this.currentPage.set(0);
    this.loadTransactions();
  }

  isIncoming(txn: TransactionResponse): boolean {
    return this.accounts().some(a => a.iban === txn.targetIban);
  }
}
