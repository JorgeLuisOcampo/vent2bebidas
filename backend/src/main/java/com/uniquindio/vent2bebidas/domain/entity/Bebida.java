package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;

import java.util.Objects;
import java.util.UUID;

public class Bebida {

    private final UUID id;
    private final String nombre;
    private final TipoEnvase tipoEnvase;

    private double precio;

    public Bebida(
            UUID id,
            String nombre,
            TipoEnvase tipoEnvase,
            double precio
    ) {

        if (id == null) {
            throw new ReglaDominioException(
                    "El identificador de la bebida es obligatorio."
            );
        }

        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException(
                    "El nombre de la bebida es obligatorio."
            );
        }

        if (tipoEnvase == null) {
            throw new ReglaDominioException(
                    "El tipo de envase es obligatorio."
            );
        }

        if (precio <= 0) {
            throw new ReglaDominioException(
                    "El precio de la bebida debe ser mayor que cero."
            );
        }

        this.id = id;
        this.nombre = nombre;
        this.tipoEnvase = tipoEnvase;
        this.precio = precio;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoEnvase getTipoEnvase() {
        return tipoEnvase;
    }

    public double getPrecio() {
        return precio;
    }

    public void cambiarPrecio(double nuevoPrecio) {

        if (nuevoPrecio <= 0 || precio == nuevoPrecio) {
            throw new ReglaDominioException(
                    "El precio debe ser mayor que cero y diferente al actual."
            );
        }

        this.precio = nuevoPrecio;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Bebida bebida)) {
            return false;
        }

        return id.equals(bebida.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
