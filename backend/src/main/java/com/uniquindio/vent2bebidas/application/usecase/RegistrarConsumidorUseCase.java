package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import com.uniquindio.vent2bebidas.domain.service.VerificadorCodigoConsumidor;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class RegistrarConsumidorUseCase {

    private final ConsumidorRepository repository;
    private final VerificadorCodigoConsumidor verificadorCodigo;

    public RegistrarConsumidorUseCase(ConsumidorRepository repository,
                                      VerificadorCodigoConsumidor verificadorCodigo) {
        this.repository = repository;
        this.verificadorCodigo = verificadorCodigo;
    }

    // El caso de uso solo ORQUESTA: pide al dominio verificar y crear, y guarda.
    // Ya no hay ningún "if" de negocio aquí: la unicidad vive en el servicio de dominio.
    @Transactional
    public Consumidor ejecutar(String nombre, CodigoConsumidor codigo, LocalDate fechaNacimiento) {
        verificadorCodigo.verificarQueEstaDisponible(codigo);
        Consumidor consumidor = Consumidor.registrar(UUID.randomUUID(), nombre, codigo, fechaNacimiento);
        repository.guardar(consumidor);
        return consumidor;
    }
}
