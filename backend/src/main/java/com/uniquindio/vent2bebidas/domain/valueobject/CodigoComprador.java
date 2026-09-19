package com.uniquindio.vent2bebidas.domain.valueobject;

public record CodigoComprador(String valor) {

    public CodigoComprador(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El valor del código comprador no puede estar vacío");
        }

        if (valor.length() != 6){
            throw new IllegalArgumentException("El valor del código comprador debe tener 6 caracteres");
        }
    }
}
