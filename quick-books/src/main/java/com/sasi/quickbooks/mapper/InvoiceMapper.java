package com.sasi.quickbooks.mapper;

import com.sasi.quickbooks.model.invoice.Invoice;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void map(Invoice updatedObject, @MappingTarget Invoice oldObject);
}
