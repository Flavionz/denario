import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TransferRequest, TransactionResponse, PagedTransactionsResponse } from '../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/transactions`;

  transfer(request: TransferRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(`${this.baseUrl}/transfer`, request);
  }

  getMyTransactions(page = 0, size = 10): Observable<PagedTransactionsResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedTransactionsResponse>(this.baseUrl, { params });
  }

  getTransactionsByIban(iban: string, page = 0, size = 10): Observable<PagedTransactionsResponse> {
    const params = new HttpParams()
      .set('iban', iban)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedTransactionsResponse>(`${this.baseUrl}/by-iban`, { params });
  }
}
