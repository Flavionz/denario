import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-account-create-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './account-create-dialog.component.html',
  styleUrl: './account-create-dialog.component.scss',
})
export class AccountCreateDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly accountService = inject(AccountService);
  private readonly dialogRef = inject(MatDialogRef<AccountCreateDialogComponent>);

  readonly loading = signal(false);

  readonly form = this.fb.group({
    ownerName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    accountType: ['CHECKING' as 'CHECKING' | 'SAVINGS', Validators.required],
    currency: ['EUR', Validators.required],
    initialDeposit: [0, [Validators.min(0)]],
  });

  readonly currencies = ['EUR', 'USD', 'GBP', 'CHF'];

  submit(): void {
    if (this.form.invalid || this.loading()) return;

    this.loading.set(true);
    const value = this.form.getRawValue();

    this.accountService.createAccount({
      ownerName: value.ownerName!,
      accountType: value.accountType!,
      currency: value.currency ?? 'EUR',
      initialDeposit: value.initialDeposit ?? 0,
    }).subscribe({
      next: (account) => {
        this.loading.set(false);
        this.dialogRef.close(account);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  cancel(): void {
    this.dialogRef.close(null);
  }
}
