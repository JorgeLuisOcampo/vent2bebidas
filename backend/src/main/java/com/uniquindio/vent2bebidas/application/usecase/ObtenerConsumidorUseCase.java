package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ObtenerConsumidorUseCase {

    private final ConsumidorRepository consumidorRepository;

    public ObtenerConsumidorUseCase(ConsumidorRepository consumidorRepository) {
        this.consumidorRepository = consumidorRepository;
    }

    // Si no existe lanza NoSuchElementException -> 404.
    public Consumidor ejecutar(UUID consumidorId) {
        return consumidorRepository.obtenerPorId(consumidorId).orElseThrow();
    }
}
