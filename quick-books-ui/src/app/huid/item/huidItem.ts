export interface HuidItem {
    huidNumber: string;
    itemName: string;
    createdOn: Date;
    saledOn?: Date;
    itemType: 'GOLD' | 'SILVER';
    grossWeight: number;
    saled: boolean;
}
