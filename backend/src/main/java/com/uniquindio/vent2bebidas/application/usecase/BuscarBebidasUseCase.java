package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.repository.Pagina;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BuscarBebidasUseCase {
    private static final int TAMANO_MAXIMO = 50;
    private final BebidaRepository repository;

    public BuscarBebidasUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    public Pagina<Bebida> ejecutar(String texto, GrupoAlcoholico grupo, int pagina, int tamano) {
        // Validaciones de ENTRADA (no son reglas del negocio): por eso lanzan
        // IllegalArgumentException (-> 400) y no ReglaDominioException (-> 422).
        if (pagina < 0) {
            throw new IllegalArgumentException("La página no puede ser negativa.");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y "
                    + TAMANO_MAXIMO + ".");
        }
        return repository.buscar(texto, grupo, pagina, tamano);
    }
}