package com.grocery.service;

import com.grocery.model.Units;
import com.grocery.repository.UnitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UnitsService {

    @Autowired
    private UnitsRepository unitsRepository;

    public Optional<Units> findByUnitName(String unitName) {
        return unitsRepository.findByUnitName(unitName);
    }

    public Units save(Units unit) {
        return unitsRepository.save(unit);
    }
}
