package com.sasi.quickbooks.model.huid;

import com.sasi.quickbooks.model.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HuidSummaryDetail {
    float totalGoldWeight;
    int totalGoldItems;
    float totalSilverWeight;
    int totalSilverItems;

    public void incrementGoldWeight(float weight) {
        totalGoldWeight += weight;
    }

    public void incrementSilverWeight(float weight) {
        totalSilverWeight += weight;
    }

    public void incrementSilverItems() {
        totalSilverItems++;
    }

    public void incrementGoldItems() {
        totalGoldItems++;
    }
}
