export interface PaymentModeDetail {
    name: string;
    amount: number;
    isChecked: boolean;
};

export const getNewPaymentDetailStateMap = (): Map<string, PaymentModeDetail> => {
    const paymentDetailStateMap: Map<string, PaymentModeDetail> = new Map([
        ['CASH', {name: 'CASH', amount: 0, isChecked: false}],
        ['CARD', {name: 'CARD', amount: 0, isChecked: false}],
        ['UPI', {name: 'UPI', amount: 0, isChecked: false}],
        ['BANK_TRANSFER', {name: 'BANK_TRANSFER', amount: 0, isChecked: false}]
    ]);
    return paymentDetailStateMap;
};
