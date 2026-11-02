package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper.ConsumidorPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// ADAPTADOR: implementa el puerto del dominio (ConsumidorRepository) usando JPA.
// La interfaz vive en domain; esta implementación vive en infrastructure.
@Repository
public class ConsumidorRepositoryJpa implements ConsumidorRepository {

    private final ConsumidorJpaDataRepository dataRepository;
    private final ConsumidorPersistenceMapper mapper;

    public ConsumidorRepositoryJpa(ConsumidorJpaDataRepository dataRepository,
                                   ConsumidorPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Consumidor> obtenerPorId(UUID id) {
        return dataRepository.findById(id.toString()).map(mapper::toDomain);
    }

    @Override
    public void guardar(Consumidor consumidor) {
        dataRepository.save(mapper.toEntity(consumidor));
    }

    @Override
    public boolean existePorCodigo(CodigoConsumidor codigo) {
        return dataRepository.existsByCodigo(codigo.valor());
    }
}