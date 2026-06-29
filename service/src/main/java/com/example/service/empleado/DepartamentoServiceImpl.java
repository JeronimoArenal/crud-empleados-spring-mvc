package com.example.service.empleado;

import com.example.domain.model.entity.empleado.Departamento;
import com.example.persistence.repository.empleado.DepartamentoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartamentoServiceImpl implements DepartamentoService{

    private final DepartamentoRepository departamentoRepository;


    //............................... Find all..........................
    @Override
    @Transactional
    public List<Departamento> findAll() {
        return departamentoRepository.findAll();
    }

    //............................... findById ..........................
    @Override
    public Optional<Departamento> findById(Long id) {
        return Optional.empty();
    }

    //............................... save ..........................
    @Override
    public Departamento saveDepartamento(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }
}
