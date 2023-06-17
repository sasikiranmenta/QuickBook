package com.sasi.quickbooks.model.huid;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HuidResponse {
    List<Huid> data;
    HuidSummary summary;
}
