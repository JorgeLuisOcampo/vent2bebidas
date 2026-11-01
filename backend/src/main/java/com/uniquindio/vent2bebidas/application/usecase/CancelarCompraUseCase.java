package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CancelarCompraUseCase {

    private final CompraRepository compraRepository;

    public CancelarCompraUseCase(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    // No hay ningún "if" de negocio aquí: quién puede cancelar y hasta cuándo
    // lo decide Compra.cancelar(...). El caso de uso solo le entrega la hora actual.
    @Transactional
    public Compra ejecutar(UUID compraId, UUID consumidorId) {
        Compra compra = compraRepository.obtenerPorId(compraId).orElseThrow();
        compra.cancelar(consumidorId, LocalDateTime.now());
        compraRepository.guardar(compra);
        return compra;
    }
}
