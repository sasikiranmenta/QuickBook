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
  paymentType?: string;
  invoiceItems: Array<Item>;
}
