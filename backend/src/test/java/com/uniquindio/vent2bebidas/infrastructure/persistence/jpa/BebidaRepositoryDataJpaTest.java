package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;
import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.repository.Pagina;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper.BebidaPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest levanta SOLO la persistencia (entidades y repositorios de Spring Data) y es más rápido.
// NO carga nuestros @Repository ni @Component, por eso hay que traerlos con @Import.
// Cada prueba se deshace sola (es transaccional por defecto).

@DataJpaTest
@Import({BebidaRepositoryJpa.class, BebidaPersistenceMapper.class})
        class BebidaRepositoryDataJpaTest {
            @Autowired
            private BebidaRepository repository;
            private Bebida publicar(String nombre) {
                return Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), nombre,
                        new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true),
                        GrupoAlcoholico.FERMENTADA, new Precio(4800, "COP"));
            }
            @Test
            void deberiaGuardarYRecuperarUnaBebida() {
// Arrange
                Bebida bebida = publicar("Zumo Prueba Solo");
// Act
                repository.guardar(bebida);
                Bebida recuperada = repository.obtenerPorId(bebida.getId()).orElseThrow();
// Assert
                assertEquals(bebida.getId(), recuperada.getId());
                assertEquals(new Precio(4800, "COP"), recuperada.getPrecio());
                assertEquals(bebida.getTipoEnvase(), recuperada.getTipoEnvase());
            }
            @Test
            void deberiaPaginarSoloLasBebidasQueGuardoLaPrueba() {
// Arrange: nombres únicos que NO existen en data.sql, así la prueba no depende de esos datos
                repository.guardar(publicar("Zumo Prueba Uno"));
                repository.guardar(publicar("Zumo Prueba Dos"));
                repository.guardar(publicar("Zumo Prueba Tres"));
// Act
                Pagina<Bebida> primera = repository.buscar("zumo prueba", null, 0, 2);
// Assert
                assertEquals(3, primera.totalElementos());
                assertEquals(2, primera.totalPaginas());
                assertEquals("Zumo Prueba Dos", primera.contenido().get(0).getNombre());
                assertEquals("Zumo Prueba Tres", primera.contenido().get(1).getNombre());
            }
            @Test
            void noDeberiaEncontrarNadaCuandoElTextoNoCoincide() {
// Act
                Pagina<Bebida> resultado =
                        repository.buscar("texto-que-no-existe-en-ninguna-bebida", null, 0, 10);
// Assert
                assertEquals(0, resultado.totalElementos());
                assertTrue(resultado.contenido().isEmpty());
            }
        }