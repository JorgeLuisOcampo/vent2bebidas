package com.uniquindio.vent2bebidas.domain.repository;

import java.util.List;

// Resultado de una búsqueda paginada. Es del dominio: no depende de Spring.
public record Pagina<T>(List<T> contenido, long totalElementos, int totalPaginas, int numero, int tamano) {
}