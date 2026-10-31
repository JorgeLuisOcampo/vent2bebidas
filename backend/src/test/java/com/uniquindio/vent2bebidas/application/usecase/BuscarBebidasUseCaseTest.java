package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.repository.Pagina;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import
        com.uniquindio.vent2bebidas.infrastructure.persistence.BebidaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BuscarBebidasUseCaseTest {
    private final BebidaRepositoryEnMemoria repository = new
            BebidaRepositoryEnMemoria();
    private final BuscarBebidasUseCase useCase = new BuscarBebidasUseCase(repository);

    private Bebida publicar(String nombre, GrupoAlcoholico grupo) {
        Bebida bebida = Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), nombre,
                new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true), grupo, new
                        Precio(5000, "COP"));
        repository.guardar(bebida);
        return bebida;
    }

    @Test
    void noDeberiaPermitirUnaPaginaNegativa() {
// Act & Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar("cerveza", null, -1,
                10));
    }

    @Test
    void noDeberiaPermitirUnTamanoDePaginaFueraDeRango() {
// Act & Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar("cerveza", null, 0,
                0));
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar("cerveza", null, 0,
                51));
    }

    @Test
    void deberiaDevolverLasBebidasDisponiblesOrdenadasPorNombreYPaginadas() {
// Arrange
        publicar("Cerveza Rubia", GrupoAlcoholico.FERMENTADA);
        publicar("Cerveza Negra", GrupoAlcoholico.FERMENTADA);
        publicar("Cerveza Roja", GrupoAlcoholico.FERMENTADA);
        publicar("Jugo de Naranja", GrupoAlcoholico.SIN_ALCOHOL);
        Bebida eliminada = publicar("Cerveza Vieja", GrupoAlcoholico.FERMENTADA);
        eliminada.eliminarLogicamente();
// Act
        Pagina<Bebida> primera = useCase.ejecutar("cerveza", null, 0, 2);
// Assert
        assertEquals(3, primera.totalElementos()); // la eliminada y el jugo no cuentan
        assertEquals(2, primera.totalPaginas());
        assertEquals("Cerveza Negra", primera.contenido().get(0).getNombre());
        assertEquals("Cerveza Roja", primera.contenido().get(1).getNombre());
    }

    @Test
    void deberiaFiltrarPorGrupoAlcoholico() {
// Arrange
        publicar("Aguardiente", GrupoAlcoholico.DESTILADA);
        publicar("Cerveza Rubia", GrupoAlcoholico.FERMENTADA);
// Act
        Pagina<Bebida> resultado = useCase.ejecutar(null, GrupoAlcoholico.DESTILADA, 0,
                10);
// Assert
        assertEquals(1, resultado.totalElementos());
        assertEquals("Aguardiente", resultado.contenido().get(0).getNombre());
    }
}
