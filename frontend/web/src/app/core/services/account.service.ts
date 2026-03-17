import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AccountSummary, CreateAccountRequest, BalanceResponse } from '../models/account.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/accounts`;

  getMyAccounts(): Observable<AccountSummary[]> {
    return this.http.get<AccountSummary[]>(this.baseUrl);
  }

  getAccountById(id: string): Observable<AccountSummary> {
    return this.http.get<AccountSummary>(`${this.baseUrl}/${id}`);
  }

  createAccount(request: CreateAccountRequest): Observable<AccountSummary> {
    return this.http.post<AccountSummary>(this.baseUrl, request);
  }

  checkBalance(iban: string, amount: number): Observable<BalanceResponse> {
    const params = new HttpParams()
      .set('iban', iban)
      .set('amount', amount.toString());
    return this.http.get<BalanceResponse>(`${this.baseUrl}/balance`, { params });
  }
}
