package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// El Value Object Precio se guarda en DOS columnas de la tabla de la bebida.
@Embeddable
public class PrecioEmbeddable {

    @Column(name = "precio_monto", nullable = false)
    private double monto;

    @Column(name = "precio_moneda", nullable = false, length = 3)
    private String moneda;

    protected PrecioEmbeddable() {
        // requerido por JPA
    }

    public PrecioEmbeddable(double monto, String moneda) {
        this.monto = monto;
        this.moneda = moneda;
    }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}