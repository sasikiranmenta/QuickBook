import {Item} from './item/item';

export interface Invoice {
  customerName: string;
  address: string;
  state: string;
  stateCode: number;
  gstin: string;
  invoiceId?: number;
  billDate: Date;
  amountBeforeTax: number;
  cgstAmount: number;
  sgstAmount: number;
  igstAmount: number;
  totalAmountAfterTax: number;
  invoiceType: string;
  paymentType: string;
  paymentMode: Array<PaymentMode>;
  totalWeight: number;
  invoiceItems: Array<Item>;
  phoneNumber: number;
}

export interface PaymentMode {
    paymentMode: string;
    amount: number;
}

export interface TotalValue {
  totalBeforeTax: number;
  totalCgst: number;
  totalSgst: number;
  totalIgst: number;
  totalAfterTax: number;
  totalWeight: number;
}
