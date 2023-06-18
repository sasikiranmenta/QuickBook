package com.sasi.quickbooks.model.huid;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class HuidResponse {
    List<Huid> data;
    HuidSummary summary;
}
