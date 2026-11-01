package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;
import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmarCompraDeBebidaUseCase {

    private final ConsumidorRepository consumidorRepository;
    private final BebidaRepository bebidaRepository;
    private final CompraRepository compraRepository;

    public ConfirmarCompraDeBebidaUseCase(ConsumidorRepository consumidorRepository,
                                          BebidaRepository bebidaRepository,
                                          CompraRepository compraRepository) {
        this.consumidorRepository = consumidorRepository;
        this.bebidaRepository = bebidaRepository;
        this.compraRepository = compraRepository;
    }

    // @Transactional: la compra y el historial del consumidor se guardan juntos o no se guarda nada.
    @Transactional
    public Compra ejecutar(UUID consumidorId, UUID bebidaId, int unidades) {
        // 1. Buscar lo que se necesita (404 si no existe)
        Consumidor consumidor = consumidorRepository.obtenerPorId(consumidorId).orElseThrow();
        Bebida bebida = bebidaRepository.obtenerPorId(bebidaId).orElseThrow();

        // 2. Pedirle al dominio que haga la operación: cada regla vive en su agregado.
        bebida.verificarQueEstaDisponible();                                   // RN-07
        consumidor.verificarPuedeComprar(bebida.getGrupoAlcoholico(), unidades); // RN-08 y RN-09
        Compra compra = Compra.confirmar(UUID.randomUUID(), consumidor.getId(), bebida.getId(),
                bebida.getVendedorId(), unidades, bebida.getPrecio(), LocalDateTime.now()); // RN-12
        consumidor.registrarCompraExitosa();

        // 3. Guardar
        compraRepository.guardar(compra);
        consumidorRepository.guardar(consumidor);
        return compra;
    }
}
