package com.uniquindio.vent2bebidas.domain.valueobject;

public enum EstadoBebida {
    PUBLICADA,
    ELIMINADA; // borrado LÓGICO: la bebida nunca se borra de verdad

    public boolean esFinal() {
        return this == ELIMINADA;
    }
}