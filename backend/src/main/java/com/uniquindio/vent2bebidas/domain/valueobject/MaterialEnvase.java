package com.uniquindio.vent2bebidas.domain.valueobject;

public enum MaterialEnvase {

    // (permiteRetorno, usosMaximos)
    VIDRIO(true, 30),
    PLASTICO(true, 10),
    ALUMINIO(false, 0);

    private final boolean permiteRetorno;
    private final int usosMaximos;

    MaterialEnvase(boolean permiteRetorno, int usosMaximos) {
        this.permiteRetorno = permiteRetorno;
        this.usosMaximos = usosMaximos;
    }

    public boolean permiteRetorno() {
        return permiteRetorno;
    }

    public int usosMaximos() {
        return usosMaximos;
    }
}