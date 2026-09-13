package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.*;

import java.util.Objects;
import java.util.UUID;

public class Envase {

    private final UUID id;
    private final TipoEnvase tipo;

    private EstadoEnvase estado;
    private int cantidadUsos;

    public Envase(UUID id, TipoEnvase tipo) {

        if (id == null) {
            throw new ReglaDominioException(
                    "El identificador del envase es obligatorio."
            );
        }

        if (tipo == null) {
            throw new ReglaDominioException(
                    "El tipo de envase es obligatorio."
            );
        }

        this.id = id;
        this.tipo = tipo;
        this.estado = EstadoEnvase.NUEVO;
        this.cantidadUsos = 0;
    }

    public UUID getId() {
        return id;
    }

    public TipoEnvase getTipo() {
        return tipo;
    }

    public EstadoEnvase getEstado() {
        return estado;
    }

    public int getCantidadUsos() {
        return cantidadUsos;
    }

    public void registrarRetorno() {

        if (!tipo.retornable()) {
            throw new ReglaDominioException(
                    "El envase no es retornable."
            );
        }

        if (estado == EstadoEnvase.DANADO) {
            throw new ReglaDominioException(
                    "Un envase dañado no puede ser retornado."
            );
        }

        if (estado == EstadoEnvase.DESCARTADO) {
            throw new ReglaDominioException(
                    "Un envase descartado no puede ser retornado."
            );
        }

        cantidadUsos++;
        estado = EstadoEnvase.RETORNADO;
    }

    public void marcarComoDanado() {

        if (estado == EstadoEnvase.DESCARTADO) {
            throw new ReglaDominioException(
                    "Un envase descartado no puede ser marcado como dañado."
            );
        }

        estado = EstadoEnvase.DANADO;
    }

    public void descartar() {
        estado = EstadoEnvase.DESCARTADO;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Envase envase)) {
            return false;
        }

        return id.equals(envase.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
