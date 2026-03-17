export type AccountType = 'CHECKING' | 'SAVINGS';
export type AccountStatus = 'ACTIVE' | 'FROZEN' | 'CLOSED';

export interface AccountSummary {
  id: string;
  iban: string;
  ownerName: string;
  balance: number;
  currency: string;
  status: AccountStatus;
  accountType: AccountType;
  createdAt: string;
}

export interface CreateAccountRequest {
  ownerName: string;
  accountType: AccountType;
  currency?: string;
  initialDeposit?: number;
}

export interface BalanceResponse {
  iban: string;
  balance: number;
  currency: string;
  sufficient: boolean;
}
