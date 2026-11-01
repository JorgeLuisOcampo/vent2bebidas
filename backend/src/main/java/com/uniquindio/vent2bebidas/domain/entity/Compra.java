package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoCompra;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Raíz del agregado Compra: registra que un consumidor compró unidades de una bebida.
 *
 * Límite del agregado: la Compra guarda solo los IDs del consumidor, la bebida y el
 * vendedor (no los objetos completos). Consumidor y Bebida son otros agregados con su
 * propio ciclo de vida; si la Compra los contuviera, cambiar una bebida "cambiaría" las
 * compras pasadas. Por la misma razón copia el precio unitario del momento de la compra.
 *
 * Invariantes:
 *  - toda compra tiene id, consumidor, bebida, vendedor, precio y fecha;
 *  - una compra lleva entre 1 y 24 unidades (RN-12);
 *  - solo el consumidor que hizo la compra puede cancelarla (RN-13);
 *  - una compra solo se cancela dentro de las 24 horas siguientes (RN-13);
 *  - una compra cancelada no vuelve a cambiar.
 */
public class Compra {

    public static final int UNIDADES_MAXIMAS_POR_COMPRA = 24;
    private static final long HORAS_PARA_CANCELAR = 24;

    private final UUID id;
    private final UUID consumidorId;
    private final UUID bebidaId;
    private final UUID vendedorId;
    private final int unidades;
    private final Precio precioUnitario;
    private final LocalDateTime fecha;
    private EstadoCompra estado;

    // Constructor privado: solo se crea con confirmar(...) o reconstruir(...)
    private Compra(UUID id, UUID consumidorId, UUID bebidaId, UUID vendedorId, int unidades,
                   Precio precioUnitario, LocalDateTime fecha, EstadoCompra estado) {
        this.id = id;
        this.consumidorId = consumidorId;
        this.bebidaId = bebidaId;
        this.vendedorId = vendedorId;
        this.unidades = unidades;
        this.precioUnitario = precioUnitario;
        this.fecha = fecha;
        this.estado = estado;
    }

    // Método de fábrica: valida TODO antes de que la compra exista.
    public static Compra confirmar(UUID id, UUID consumidorId, UUID bebidaId, UUID vendedorId,
                                   int unidades, Precio precioUnitario, LocalDateTime fecha) {
        if (id == null) {
            throw new ReglaDominioException("El identificador de la compra es obligatorio.");
        }
        if (consumidorId == null) {
            throw new ReglaDominioException("La compra debe tener un consumidor.");
        }
        if (bebidaId == null || vendedorId == null) {
            throw new ReglaDominioException("La compra debe indicar la bebida y su vendedor.");
        }
        if (precioUnitario == null) {
            throw new ReglaDominioException("El precio de la compra es obligatorio.");
        }
        if (fecha == null) {
            throw new ReglaDominioException("La fecha de la compra es obligatoria.");
        }
        if (unidades < 1 || unidades > UNIDADES_MAXIMAS_POR_COMPRA) {
            throw new ReglaDominioException("Una compra debe tener entre 1 y "
                    + UNIDADES_MAXIMAS_POR_COMPRA + " unidades.");
        }
        return new Compra(id, consumidorId, bebidaId, vendedorId, unidades, precioUnitario,
                fecha, EstadoCompra.CONFIRMADA);
    }

    // SOLO para la capa de persistencia: reconstruye una compra que ya existía.
    public static Compra reconstruir(UUID id, UUID consumidorId, UUID bebidaId, UUID vendedorId,
                                     int unidades, Precio precioUnitario, LocalDateTime fecha,
                                     EstadoCompra estado) {
        return new Compra(id, consumidorId, bebidaId, vendedorId, unidades, precioUnitario, fecha, estado);
    }

    // Valida PRIMERO y cambia el estado DESPUÉS.
    // "ahora" se recibe como parámetro: el dominio no lee el reloj del sistema (así se puede probar).
    public void cancelar(UUID consumidorSolicitante, LocalDateTime ahora) {
        if (consumidorSolicitante == null || !consumidorId.equals(consumidorSolicitante)) {
            throw new ReglaDominioException("Solo el consumidor que hizo la compra puede cancelarla.");
        }
        if (estado.esFinal()) {
            throw new ReglaDominioException("La compra ya está cancelada.");
        }
        if (ahora == null || ahora.isAfter(fecha.plusHours(HORAS_PARA_CANCELAR))) {
            throw new ReglaDominioException("Una compra solo puede cancelarse dentro de las "
                    + HORAS_PARA_CANCELAR + " horas siguientes a su confirmación.");
        }
        this.estado = EstadoCompra.CANCELADA;
    }

    // Dato calculado por el dominio: no se guarda en la base.
    public Precio total() {
        return precioUnitario.multiplicarPor(unidades);
    }

    // Sin setters: solo lectura hacia afuera.
    public UUID getId() { return id; }
    public UUID getConsumidorId() { return consumidorId; }
    public UUID getBebidaId() { return bebidaId; }
    public UUID getVendedorId() { return vendedorId; }
    public int getUnidades() { return unidades; }
    public Precio getPrecioUnitario() { return precioUnitario; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoCompra getEstado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compra otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
