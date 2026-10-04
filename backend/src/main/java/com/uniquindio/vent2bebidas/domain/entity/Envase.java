package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;

import java.util.Objects;
import java.util.UUID;

public class Envase {

    private final UUID id;
    private final TipoEnvase tipo;
    private EstadoEnvase estado;
    private int cantidadUsos;

    private Envase(UUID id, TipoEnvase tipo) {
        this.id = id;
        this.tipo = tipo;
        this.estado = EstadoEnvase.NUEVO;
        this.cantidadUsos = 0;
    }

    public static Envase registrar(UUID id, TipoEnvase tipo) {
        if (id == null) {
            throw new ReglaDominioException("El identificador del envase es obligatorio.");
        }
        if (tipo == null) {
            throw new ReglaDominioException("El tipo de envase es obligatorio.");
        }
        return new Envase(id, tipo);
    }

    public void registrarRetorno() {
        if (!tipo.retornable()) {
            throw new ReglaDominioException("El envase no es retornable.");
        }
        verificarQueNoEstaDescartado();
        if (estado == EstadoEnvase.DANADO) {
            throw new ReglaDominioException("Un envase dañado no puede ser retornado.");
        }
        if (cantidadUsos >= tipo.usosMaximos()) {
            throw new ReglaDominioException(
                    "El envase alcanzó su límite de usos y debe descartarse.");
        }
        verificarTransicion(EstadoEnvase.RETORNADO);
        cantidadUsos++;
        estado = EstadoEnvase.RETORNADO;
    }

    public void marcarComoDanado() {
        verificarQueNoEstaDescartado();
        verificarTransicion(EstadoEnvase.DANADO);
        estado = EstadoEnvase.DANADO;
    }

    public void descartar() {
        verificarQueNoEstaDescartado();
        verificarTransicion(EstadoEnvase.DESCARTADO);
        estado = EstadoEnvase.DESCARTADO;
    }

    public int usosRestantes() {
        return tipo.usosMaximos() - cantidadUsos;
    }

    private void verificarQueNoEstaDescartado() {
        if (estado.esFinal()) {
            throw new ReglaDominioException("Un envase descartado no puede modificarse.");
        }
    }

    private void verificarTransicion(EstadoEnvase siguiente) {
        if (!estado.puedeTransicionarA(siguiente)) {
            throw new ReglaDominioException("No se puede pasar de " + estado + " a " + siguiente);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Envase envase)) return false;
        return id.equals(envase.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}