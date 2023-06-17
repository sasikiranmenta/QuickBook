package com.sasi.quickbooks.model.huid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "huid")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Huid {
    @Id
    String huidNumber;
    String itemName;
    Date createdOn;
    Date saledOn;
    Float grossWeight;
    boolean isSaled;
}
