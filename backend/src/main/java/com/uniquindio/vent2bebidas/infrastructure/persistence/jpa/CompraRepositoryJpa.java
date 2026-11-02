package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.repository.BebidaVendida;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoCompra;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper.CompraPersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ADAPTADOR: implementa el puerto del dominio (CompraRepository) usando JPA.
// Aquí se convierte la paginación de Spring (PageRequest): el dominio solo pide "limite".
@Repository
public class CompraRepositoryJpa implements CompraRepository {

    private final CompraJpaDataRepository dataRepository;
    private final CompraPersistenceMapper mapper;

    public CompraRepositoryJpa(CompraJpaDataRepository dataRepository, CompraPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Compra> obtenerPorId(UUID id) {
        return dataRepository.findById(id.toString()).map(mapper::toDomain);
    }

    @Override
    public void guardar(Compra compra) {
        dataRepository.save(mapper.toEntity(compra));
    }

    @Override
    public List<Compra> listarPorConsumidor(UUID consumidorId) {
        return dataRepository.findByConsumidorIdOrderByFechaDesc(consumidorId.toString()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<BebidaVendida> masVendidas(int limite) {
        return dataRepository.masVendidas(EstadoCompra.CONFIRMADA, EstadoBebida.PUBLICADA,
                        PageRequest.of(0, limite)).stream()
                .map(fila -> new BebidaVendida(
                        UUID.fromString(fila.bebidaId()),
                        fila.nombre(),
                        fila.unidadesVendidas(),
                        fila.numeroCompras()))
                .toList();
    }
}