package com.sasi.quickbooks.service;

import com.sasi.quickbooks.Repository.ItemModelRepository;
import com.sasi.quickbooks.model.huid.ItemModels;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemModelsService {

    private final ItemModelRepository repository;

    public void save(ItemModels itemModels) {
        repository.save(itemModels);
    }

    public ItemModels findByItemModelType(String itemModelType) {
        return repository.findItemModelsByItemType(itemModelType);
    }
}
