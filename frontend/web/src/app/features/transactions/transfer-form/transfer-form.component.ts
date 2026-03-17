import { Component, inject, signal, input, output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TransactionService } from '../../../core/services/transaction.service';
import { AccountSummary } from '../../../core/models/account.model';
import { IbanFormatPipe } from '../../../shared/pipes/iban-format.pipe';

function notSameIban(control: AbstractControl): ValidationErrors | null {
  const form = control.parent;
  if (!form) return null;
  const source = form.get('sourceIban')?.value;
  const target = form.get('targetIban')?.value;
  if (source && target && source === target) {
    return { sameIban: true };
  }
  return null;
}

@Component({
  selector: 'app-transfer-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    IbanFormatPipe,
  ],
  templateUrl: './transfer-form.component.html',
  styleUrl: './transfer-form.component.scss',
})
export class TransferFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly transactionService = inject(TransactionService);
  private readonly snackBar = inject(MatSnackBar);

  readonly accounts = input<AccountSummary[]>([]);
  readonly transferred = output<void>();

  readonly loading = signal(false);

  readonly form = this.fb.group({
    sourceIban: ['', Validators.required],
    targetIban: ['', [Validators.required, notSameIban]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    currency: ['EUR', Validators.required],
    description: [''],
  });

  readonly currencies = ['EUR', 'USD', 'GBP', 'CHF'];

  ngOnInit(): void {
    this.form.get('sourceIban')?.valueChanges.subscribe(() => {
      this.form.get('targetIban')?.updateValueAndValidity();
    });
  }

  submit(): void {
    if (this.form.invalid || this.loading()) return;

    this.loading.set(true);
    const v = this.form.getRawValue();

    this.transactionService.transfer({
      sourceIban: v.sourceIban!,
      targetIban: v.targetIban!,
      amount: v.amount!,
      currency: v.currency!,
      description: v.description ?? undefined,
    }).subscribe({
      next: () => {
        this.loading.set(false);
        this.form.reset({ currency: 'EUR', amount: null });
        this.snackBar.open('Transfer completed successfully!', 'Close', {
          duration: 3000,
          panelClass: ['snack-success'],
        });
        this.transferred.emit();
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  reset(): void {
    this.form.reset({ currency: 'EUR', amount: null });
  }
}
