package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;
import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.repository.Pagina;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

// Levanta TODA la aplicación y usa la base H2 de verdad (con los datos de data.sql).
// @Transactional deshace al terminar cada prueba lo que se haya guardado.
// Se pide el PUERTO del dominio (BebidaRepository), no el adaptador: la prueba no sabe que por debajo es JPA.

@SpringBootTest
@Transactional
        class BebidaRepositoryJpaTest {
            @Autowired
            private BebidaRepository repository;
            @Test
            void deberiaGuardarYRecuperarUnaBebidaConSusValueObjects() {
// Arrange
                UUID id = UUID.randomUUID();
                UUID vendedorId = UUID.randomUUID();
                TipoEnvase tipo = new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(500),
                        true);
                Bebida bebida = Bebida.publicar(id, vendedorId, "Cerveza de Prueba", tipo,
                        GrupoAlcoholico.FERMENTADA, new Precio(7500, "COP"));
// Act
                repository.guardar(bebida);
                Bebida recuperada = repository.obtenerPorId(id).orElseThrow();
// Assert: los Value Objects (Precio y TipoEnvase) y los enums vuelven intactos
                assertEquals(id, recuperada.getId());
                assertEquals(vendedorId, recuperada.getVendedorId());
                assertEquals("Cerveza de Prueba", recuperada.getNombre());
                assertEquals(new Precio(7500, "COP"), recuperada.getPrecio());
                assertEquals(tipo, recuperada.getTipoEnvase());
                assertEquals(GrupoAlcoholico.FERMENTADA, recuperada.getGrupoAlcoholico());
                assertEquals(EstadoBebida.PUBLICADA, recuperada.getEstado());
            }
            @Test
            void deberiaGuardarElEstadoEliminadaDeUnaBebida() {
// Arrange
                Bebida bebida = Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), "Bebida a Eliminar",
                new TipoEnvase(MaterialEnvase.PLASTICO, new Capacidad(500), true),
                        GrupoAlcoholico.SIN_ALCOHOL, new Precio(3000, "COP"));
                bebida.eliminarLogicamente();
// Act
                repository.guardar(bebida);
                Bebida recuperada = repository.obtenerPorId(bebida.getId()).orElseThrow();
// Assert
                assertEquals(EstadoBebida.ELIMINADA, recuperada.getEstado());
                assertFalse(recuperada.estaDisponible());
            }
            @Test
            void deberiaPaginarLaBusquedaEnOrdenAlfabeticoConLosDatosDeDataSql() {
// Arrange: data.sql tiene 3 cervezas disponibles (Negra, Roja y Rubia) y 1 eliminada
// Act
                Pagina<Bebida> primera = repository.buscar("cerveza", null, 0, 2);
                Pagina<Bebida> segunda = repository.buscar("cerveza", null, 1, 2);
// Assert
                assertEquals(3, primera.totalElementos());
                assertEquals(2, primera.totalPaginas());
                assertEquals("Cerveza Negra Artesanal", primera.contenido().get(0).getNombre());
                assertEquals("Cerveza Roja Artesanal", primera.contenido().get(1).getNombre());
                assertEquals(1, segunda.contenido().size());
                assertEquals("Cerveza Rubia Artesanal", segunda.contenido().get(0).getNombre());
            }
            @Test
            void deberiaFiltrarPorGrupoAlcoholico() {
// Act
                Pagina<Bebida> destiladas = repository.buscar("", GrupoAlcoholico.DESTILADA, 0,
                        10);
// Assert: data.sql tiene exactamente 2 destiladas
                assertEquals(2, destiladas.totalElementos());
                assertEquals("Aguardiente Tradicional", destiladas.contenido().get(0).getNombre());
                assertEquals("Ron Anejo", destiladas.contenido().get(1).getNombre());
            }
            @Test
            void noDeberiaIncluirLasBebidasEliminadasEnLaBusqueda() {
// Act: "Cerveza Descatalogada" existe en data.sql, pero está ELIMINADA
                Pagina<Bebida> resultado = repository.buscar("descatalogada", null, 0, 10);
// Assert
                assertEquals(0, resultado.totalElementos());
            }
        }