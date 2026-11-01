package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListarComprasDelConsumidorUseCase {

    private final CompraRepository compraRepository;

    public ListarComprasDelConsumidorUseCase(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public List<Compra> ejecutar(UUID consumidorId) {
        return compraRepository.listarPorConsumidor(consumidorId);
    }
}
