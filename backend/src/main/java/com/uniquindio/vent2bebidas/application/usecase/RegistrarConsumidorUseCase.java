package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class RegistrarConsumidorUseCase {

    private final ConsumidorRepository repository;

    public RegistrarConsumidorUseCase(ConsumidorRepository repository) {
        this.repository = repository;
    }

    public Consumidor ejecutar(String nombre, CodigoConsumidor codigo, LocalDate fechaNacimiento) {
        // Única regla que NO puede vivir dentro de la entidad: la unicidad del código
        // exige consultar a TODOS los consumidores, y una entidad solo se conoce a sí misma.
        if (repository.existePorCodigo(codigo)) {
            throw new ReglaDominioException("Ya existe un consumidor con ese código.");
        }
        Consumidor consumidor = Consumidor.registrar(UUID.randomUUID(), nombre, codigo, fechaNacimiento);
        repository.guardar(consumidor);
        return consumidor;
    }
}