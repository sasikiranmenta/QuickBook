package com.sasi.quickbooks.model.huid;

import com.sasi.quickbooks.model.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HuidSummaryDetail {
    float totalWeight;
    float totalItems;
    ItemTypeEnum itemType;
    HuidItemStatusEnum status;
}
