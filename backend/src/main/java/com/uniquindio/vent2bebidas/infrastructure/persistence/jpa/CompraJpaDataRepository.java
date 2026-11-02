package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoCompra;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.CompraEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Sin "public": solo el adaptador de este paquete lo usa.
interface CompraJpaDataRepository extends JpaRepository<CompraEntity, String> {

    List<CompraEntity> findByConsumidorIdOrderByFechaDesc(String consumidorId);

    // RANKING: lo calcula la BASE DE DATOS, no Java.
    //  - JOIN     une cada compra con su bebida para obtener el nombre
    //  - WHERE    solo compras CONFIRMADAS y bebidas que siguen PUBLICADAS
    //  - GROUP BY agrupa todas las compras de la misma bebida
    //  - SUM      suma las unidades vendidas; COUNT cuenta cuántas compras hubo
    //  - Pageable limita el número de filas (TOP N)
    @Query("""
            SELECT new com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.BebidaVendidaFila(
                       b.id, b.nombre, SUM(c.unidades), COUNT(c))
            FROM CompraEntity c
            JOIN BebidaEntity b ON b.id = c.bebidaId
            WHERE c.estado = :estadoCompra
              AND b.estado = :estadoBebida
            GROUP BY b.id, b.nombre
            ORDER BY SUM(c.unidades) DESC, b.nombre ASC
            """)
    List<BebidaVendidaFila> masVendidas(@Param("estadoCompra") EstadoCompra estadoCompra,
                                        @Param("estadoBebida") EstadoBebida estadoBebida,
                                        Pageable pageable);
}