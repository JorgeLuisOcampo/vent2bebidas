package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarBebidasDisponiblesUseCase {

    private final BebidaRepository repository;

    public ListarBebidasDisponiblesUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    public List<Bebida> ejecutar() {
        return repository.listarDisponibles();
    }
}