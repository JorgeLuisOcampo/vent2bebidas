package com.uniquindio.vent2bebidas.domain.service;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;


public class VerificadorCodigoConsumidor {

    private final ConsumidorRepository consumidorRepository;

    public VerificadorCodigoConsumidor(ConsumidorRepository consumidorRepository) {
        this.consumidorRepository = consumidorRepository;
    }

    public void verificarQueEstaDisponible(CodigoConsumidor codigo) {
        if (codigo == null) {
            throw new ReglaDominioException("El código de consumidor es obligatorio.");
        }
        if (consumidorRepository.existePorCodigo(codigo)) {
            throw new ReglaDominioException("Ya existe un consumidor con el código " + codigo.valor() + ".");
        }
    }
}
