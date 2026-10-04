package com.uniquindio.vent2bebidas.domain.valueobject;

public enum EstadoEnvase {
    NUEVO,
    RETORNADO,
    DANADO,
    DESCARTADO;

    public boolean puedeTransicionarA(EstadoEnvase siguiente) {
        return switch (this) {
            case NUEVO      -> siguiente == RETORNADO || siguiente == DANADO || siguiente == DESCARTADO;
            case RETORNADO  -> siguiente == RETORNADO || siguiente == DANADO || siguiente == DESCARTADO;
            case DANADO     -> siguiente == DESCARTADO;
            case DESCARTADO -> false; // estado final: no admite salida
        };
    }

    public boolean esFinal() {
        return this == DESCARTADO;
    }
}