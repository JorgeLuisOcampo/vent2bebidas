package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.repository.Pagina;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.BebidaEntity;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper.BebidaPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

// ADAPTADOR: implementa el puerto del dominio (BebidaRepository) usando JPA.
@Repository
public class BebidaRepositoryJpa implements BebidaRepository {

    private final BebidaJpaDataRepository dataRepository;
    private final BebidaPersistenceMapper mapper;

    public BebidaRepositoryJpa(BebidaJpaDataRepository dataRepository, BebidaPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Bebida> obtenerPorId(UUID id) {
        return dataRepository.findById(id.toString()).map(mapper::toDomain);
    }

    @Override
    public void guardar(Bebida bebida) {
        dataRepository.save(mapper.toEntity(bebida));
    }

    @Override
    public List<Bebida> listarDisponibles() {
        return dataRepository.findByEstadoOrderByNombreAsc(EstadoBebida.PUBLICADA).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Pagina<Bebida> buscar(String texto, GrupoAlcoholico grupo, int pagina, int tamano) {
        String patron = (texto == null) ? "" : texto.trim();
        Set<GrupoAlcoholico> grupos = (grupo == null)
                ? EnumSet.allOf(GrupoAlcoholico.class)
                : EnumSet.of(grupo);

        Page<BebidaEntity> resultado = dataRepository.buscar(
                EstadoBebida.PUBLICADA, patron, grupos, PageRequest.of(pagina, tamano, Sort.by("nombre")));

        return new Pagina<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.getNumber(),
                resultado.getSize());
    }
}