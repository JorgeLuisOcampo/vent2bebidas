package com.uniquindio.vent2bebidas.domain.valueobject;

// Estados por los que pasa una compra. CANCELADA es final: no admite más cambios.
public enum EstadoCompra {
    CONFIRMADA,
    CANCELADA;

    public boolean esFinal() {
        return this == CANCELADA;
    }
}
