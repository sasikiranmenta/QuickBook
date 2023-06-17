export interface HuidRequestBody {
    from: Date;
    to: Date;
    applyDateRangeOn: string;
    includeSaledData: boolean;
    includeStockData: boolean;
}
