package com.uniquindio.vent2bebidas.domain.valueobject;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;

public record CodigoConsumidor(String valor) {

    public CodigoConsumidor(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El valor del código consumidor no puede estar vacío");
        }
        if (valor.length() != 6){
            throw new ReglaDominioException("El valor del código consumidor debe tener 6 caracteres");
        }
        this.valor = valor;
    }
}
