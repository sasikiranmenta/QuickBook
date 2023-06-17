package com.sasi.quickbooks.controller;

import com.sasi.quickbooks.model.huid.ItemModels;
import com.sasi.quickbooks.service.ItemModelsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("quick-book/itemModels")
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
public class ItemModelsController {

    final ItemModelsService itemModelsService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity saveItemModel(@RequestBody @Valid ItemModels itemModel) {
        this.itemModelsService.save(itemModel);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/getByItemModel", method = RequestMethod.GET)
    public ResponseEntity<ItemModels> getItemModel(@RequestParam(name = "itemModelType") String itemModelType) {
        return ResponseEntity.status(HttpStatus.OK).body(itemModelsService.findByItemModelType(itemModelType));
    }

}
