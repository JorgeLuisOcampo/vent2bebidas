package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

// Proyección que llena la consulta JPQL del ranking con "SELECT new ...".
// Vive en infraestructura porque usa los tipos de la base (id como String, Long de SUM/COUNT);
// el adaptador la traduce al record del dominio BebidaVendida.
public record BebidaVendidaFila(String bebidaId, String nombre, Long unidadesVendidas, Long numeroCompras) {
}