package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConfirmarCompraDeBebidaUseCase {

    private final ConsumidorRepository consumidorRepository;
    private final BebidaRepository bebidaRepository;

    public ConfirmarCompraDeBebidaUseCase(ConsumidorRepository consumidorRepository,
                                          BebidaRepository bebidaRepository) {
        this.consumidorRepository = consumidorRepository;
        this.bebidaRepository = bebidaRepository;
    }

    public void ejecutar(UUID consumidorId, UUID bebidaId, int unidades) {
        Consumidor consumidor = consumidorRepository.obtenerPorId(consumidorId).orElseThrow();
        Bebida bebida = bebidaRepository.obtenerPorId(bebidaId).orElseThrow();

        // Este caso de uso solo ORQUESTA: cada regla vive en su agregado.
        bebida.verificarQueEstaDisponible();
        consumidor.verificarPuedeComprar(bebida.getGrupoAlcoholico(), unidades);
        consumidor.registrarCompraExitosa();

        consumidorRepository.guardar(consumidor);
    }
}