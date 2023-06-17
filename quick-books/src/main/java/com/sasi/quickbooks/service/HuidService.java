package com.sasi.quickbooks.service;

import com.sasi.quickbooks.Repository.HuidRepository;
import com.sasi.quickbooks.model.huid.Huid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HuidService {

    private final HuidRepository repository;

    public void save(Huid huid) {
        repository.save(huid);
    }

    public Huid findByHuidNumber(String huidNumber) {
        return repository.findById(huidNumber).orElse(null);
    }

    public List<Huid> fetchAllHuid() {
        return repository.findAll();
    }
}
