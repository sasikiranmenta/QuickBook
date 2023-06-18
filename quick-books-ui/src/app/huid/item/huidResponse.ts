import {HuidItem} from "./huidItem";

export interface HuidResponse {
    data: Array<HuidItem>;
    summary: HuidSummary;
}

export interface HuidSummary {
    from: Date;
    to: Date;
    stockDetails: HuidSummaryDetail;
    saleDetails: HuidSummaryDetail;
}

export interface HuidSummaryDetail {
    totalGoldWeight: number;
    totalGoldItems: number;
    totalSilverWeight: number;
    totalSilverItems: number;
}
