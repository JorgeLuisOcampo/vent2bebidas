package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.repository.BebidaVendida;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoCompra;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper.BebidaPersistenceMapper;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper.CompraPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest: solo la capa de persistencia, sobre H2. Cada prueba se deshace sola.
// Se importan los adaptadores y mappers porque @DataJpaTest no carga nuestros @Repository/@Component.
@DataJpaTest
@Import({CompraRepositoryJpa.class, CompraPersistenceMapper.class,
        BebidaRepositoryJpa.class, BebidaPersistenceMapper.class})
class CompraRepositoryDataJpaTest {

    @Autowired
    private CompraRepository compras;

    @Autowired
    private BebidaRepository bebidas;

    private Bebida publicar(String nombre) {
        Bebida bebida = Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), nombre,
                new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true),
                GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
        bebidas.guardar(bebida);
        return bebida;
    }

    private Compra comprar(Bebida bebida, UUID consumidorId, int unidades, LocalDateTime fecha) {
        Compra compra = Compra.confirmar(UUID.randomUUID(), consumidorId, bebida.getId(),
                bebida.getVendedorId(), unidades, bebida.getPrecio(), fecha);
        compras.guardar(compra);
        return compra;
    }

    @Test
    void deberiaGuardarYRecuperarUnaCompraConSuPrecioYEstado() {
        // Arrange
        Bebida bebida = publicar("Ranking Prueba Guardar");
        Compra compra = comprar(bebida, UUID.randomUUID(), 3, LocalDateTime.now());

        // Act
        Compra recuperada = compras.obtenerPorId(compra.getId()).orElseThrow();

        // Assert
        assertEquals(3, recuperada.getUnidades());
        assertEquals(new Precio(5000, "COP"), recuperada.getPrecioUnitario());
        assertEquals(EstadoCompra.CONFIRMADA, recuperada.getEstado());
    }

    @Test
    void elRankingSumaUnidadesYExcluyeComprasCanceladasYBebidasEliminadas() {
        // Arrange
        UUID consumidor = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        Bebida masVendida = publicar("Ranking Prueba Ganadora");
        comprar(masVendida, consumidor, 24, ahora);
        comprar(masVendida, consumidor, 24, ahora);
        comprar(masVendida, consumidor, 24, ahora); // 72 unidades en 3 compras

        Bebida conCanceladas = publicar("Ranking Prueba Cancelada");
        for (int i = 0; i < 4; i++) {
            Compra compra = comprar(conCanceladas, consumidor, 24, ahora);
            compra.cancelar(consumidor, ahora.plusHours(1));
            compras.guardar(compra); // 96 unidades, pero todas canceladas
        }

        Bebida eliminada = publicar("Ranking Prueba Eliminada");
        for (int i = 0; i < 4; i++) {
            comprar(eliminada, consumidor, 24, ahora); // 96 unidades de una bebida que se retira
        }
        eliminada.eliminarLogicamente();
        bebidas.guardar(eliminada);

        // Act
        List<BebidaVendida> ranking = compras.masVendidas(3);

        // Assert
        assertEquals(3, ranking.size());
        assertEquals(masVendida.getId(), ranking.get(0).bebidaId());
        assertEquals(72, ranking.get(0).unidadesVendidas());
        assertEquals(3, ranking.get(0).numeroCompras());
        assertTrue(ranking.stream().noneMatch(f -> f.bebidaId().equals(conCanceladas.getId())));
        assertTrue(ranking.stream().noneMatch(f -> f.bebidaId().equals(eliminada.getId())));
    }

    @Test
    void deberiaListarLasComprasDeUnConsumidorDeLaMasRecienteALaMasAntigua() {
        // Arrange
        UUID consumidor = UUID.randomUUID();
        Bebida bebida = publicar("Ranking Prueba Historial");
        Compra vieja = comprar(bebida, consumidor, 1, LocalDateTime.now().minusDays(2));
        Compra nueva = comprar(bebida, consumidor, 2, LocalDateTime.now());
        comprar(bebida, UUID.randomUUID(), 5, LocalDateTime.now()); // de otro consumidor

        // Act
        List<Compra> resultado = compras.listarPorConsumidor(consumidor);

        // Assert
        assertEquals(List.of(nueva, vieja), resultado);
    }
}