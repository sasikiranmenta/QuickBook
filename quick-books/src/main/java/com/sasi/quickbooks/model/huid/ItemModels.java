package com.sasi.quickbooks.model.huid;

import com.sasi.quickbooks.model.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document(collection = "itemModels")
@Getter
@Setter
@TypeAlias("ItemModels")
public class ItemModels {
    @Id
    ItemTypeEnum itemType;
    Set<String> models;
}
