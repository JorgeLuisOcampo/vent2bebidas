package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.BebidaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

// Sin "public": solo el adaptador de este paquete lo usa. El dominio nunca lo ve.
interface BebidaJpaDataRepository extends JpaRepository<BebidaEntity, String> {

    List<BebidaEntity> findByEstadoOrderByNombreAsc(EstadoBebida estado);

    @Query("""
            SELECT b FROM BebidaEntity b
            WHERE b.estado = :estado
              AND LOWER(b.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
              AND b.grupoAlcoholico IN (:grupos)
            """)
    Page<BebidaEntity> buscar(@Param("estado") EstadoBebida estado,
                              @Param("texto") String texto,
                              @Param("grupos") Collection<GrupoAlcoholico> grupos,
                              Pageable pageable);
}