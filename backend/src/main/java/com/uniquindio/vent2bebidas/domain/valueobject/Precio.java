package com.uniquindio.vent2bebidas.domain.valueobject;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;

public record Precio(double monto, String moneda) {

    public Precio {
        if (!(monto > 0) || Double.isInfinite(monto)) {
            throw new ReglaDominioException("El precio debe ser mayor que cero.");
        }
        if (moneda == null || moneda.isBlank()) {
            throw new ReglaDominioException("La moneda del precio es obligatoria.");
        }
        moneda = moneda.trim().toUpperCase();
        if (moneda.length() != 3) {
            throw new ReglaDominioException("La moneda debe ser un código de 3 letras (ej. COP).");
        }
    }

    public Precio conDescuento(int porcentaje) {
        if (porcentaje < 0 || porcentaje > 99) {
            throw new ReglaDominioException("El descuento debe estar entre 0 y 99 por ciento.");
        }
        return new Precio(monto * (100 - porcentaje) / 100.0, moneda);
    }

    public boolean tieneLaMismaMonedaQue(Precio otro) {
        return this.moneda.equals(otro.moneda);
    }
}