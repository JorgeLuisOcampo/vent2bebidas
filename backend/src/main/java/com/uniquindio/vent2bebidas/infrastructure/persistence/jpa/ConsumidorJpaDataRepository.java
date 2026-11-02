package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.ConsumidorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Sin "public": solo el adaptador de este paquete lo usa. El dominio nunca lo ve.
interface ConsumidorJpaDataRepository extends JpaRepository<ConsumidorEntity, String> {

    // Spring Data genera: SELECT COUNT(*) > 0 FROM consumidores WHERE codigo = ?
    boolean existsByCodigo(String codigo);
}