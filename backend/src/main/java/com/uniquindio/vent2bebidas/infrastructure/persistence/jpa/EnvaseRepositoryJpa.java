package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.repository.EnvaseRepository;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper.EnvasePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// ADAPTADOR: implementa el puerto del dominio (EnvaseRepository) usando JPA.
@Repository
public class EnvaseRepositoryJpa implements EnvaseRepository {

    private final EnvaseJpaDataRepository dataRepository;
    private final EnvasePersistenceMapper mapper;

    public EnvaseRepositoryJpa(EnvaseJpaDataRepository dataRepository, EnvasePersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Envase> obtenerPorId(UUID id) {
        return dataRepository.findById(id.toString()).map(mapper::toDomain);
    }

    @Override
    public void guardar(Envase envase) {
        dataRepository.save(mapper.toEntity(envase));
    }
}