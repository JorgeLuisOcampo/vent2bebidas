package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.EnvaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Sin "public": solo el adaptador de este paquete lo usa.
interface EnvaseJpaDataRepository extends JpaRepository<EnvaseEntity, String> {
}