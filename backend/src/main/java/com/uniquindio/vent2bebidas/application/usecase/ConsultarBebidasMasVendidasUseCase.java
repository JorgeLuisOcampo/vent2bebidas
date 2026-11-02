package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.repository.BebidaVendida;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultarBebidasMasVendidasUseCase {

    private static final int LIMITE_MAXIMO = 20;
    private final CompraRepository compraRepository;

    public ConsultarBebidasMasVendidasUseCase(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    // El cálculo (JOIN, GROUP BY, SUM, COUNT) lo hace la base de datos en el adaptador JPA.
    // Aquí solo se valida la ENTRADA (no es una regla del negocio) -> 400 si es inválida.
    public List<BebidaVendida> ejecutar(int limite) {
        if (limite < 1 || limite > LIMITE_MAXIMO) {
            throw new IllegalArgumentException("El límite del ranking debe estar entre 1 y " + LIMITE_MAXIMO + ".");
        }
        return compraRepository.masVendidas(limite);
    }
}