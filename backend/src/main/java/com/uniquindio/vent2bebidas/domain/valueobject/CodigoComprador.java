package com.uniquindio.vent2bebidas.domain.valueobject;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;

public record CodigoComprador(String valor) {

    public CodigoComprador(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El valor del código comprador no puede estar vacío");
        }
        if (valor.length() != 6){
            throw new IllegalArgumentException("El valor del código comprador debe tener 6 caracteres");
        }
        this.valor = valor;
    }
}
