package com.uniquindio.vent2bebidas.domain.valueobject;

public enum GrupoAlcoholico {
    SIN_ALCOHOL(0.0),
    FERMENTADA(5.0),
    DESTILADA(40.0);

    private final double porcentajeAlcoholBase;

    GrupoAlcoholico(double porcentajeAlcoholBase) {
        this.porcentajeAlcoholBase = porcentajeAlcoholBase;
    }

    public double getPorcentajeAlcoholBase() {
        return porcentajeAlcoholBase;
    }

    public boolean requiereMayoriaEdad(){
        return this != SIN_ALCOHOL;
    }
}
