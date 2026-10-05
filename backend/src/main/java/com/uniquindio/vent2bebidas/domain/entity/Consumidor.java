package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.UUID;

/**
 * Raíz del agregado Consumidor.
 *
 * Invariantes que garantiza:
 *  - todo consumidor tiene id, nombre, código válido y fecha de nacimiento no futura;
 *  - el historial de compras nunca es negativo y solo crece de uno en uno;
 *  - un menor de edad nunca puede comprar bebidas alcohólicas;
 *  - comprar más de 5 unidades de una vez exige más de 3 compras históricas;
 *  - toda compra debe ser de al menos una unidad.
 */
public class Consumidor {

    private static final int EDAD_MINIMA_PARA_ALCOHOL = 18;
    private static final int UNIDADES_SIN_RESTRICCION = 5;
    private static final int COMPRAS_NECESARIAS_PARA_VOLUMEN = 3;

    private final UUID id;
    private final String nombre;
    private final CodigoConsumidor codigoConsumidor;
    private final LocalDate fechaNacimiento;
    private int comprasHistoricas;

    // Constructor privado: nadie crea un Consumidor sin pasar por registrar(...)
    private Consumidor(UUID id, String nombre, CodigoConsumidor codigoConsumidor,
                       LocalDate fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.codigoConsumidor = codigoConsumidor;
        this.fechaNacimiento = fechaNacimiento;
        this.comprasHistoricas = 0; // todo consumidor nace sin compras
    }

    // Único punto de entrada para crear un Consumidor: valida ANTES de existir
    public static Consumidor registrar(UUID id, String nombre, CodigoConsumidor codigoConsumidor,
                                       LocalDate fechaNacimiento) {
        if (id == null) {
            throw new ReglaDominioException("El id del consumidor es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El nombre del consumidor es obligatorio.");
        }
        if (codigoConsumidor == null) {
            throw new ReglaDominioException("El consumidor necesita un código válido para acceder al catálogo.");
        }
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new ReglaDominioException("La fecha de nacimiento no es válida.");
        }
        return new Consumidor(id, nombre.trim(), codigoConsumidor, fechaNacimiento);
    }

    // ---------- Comportamiento del negocio ----------
    // Cada método valida PRIMERO, cambia el estado DESPUÉS — nunca al revés.

    // Las reglas de compra viven AQUÍ, no en el caso de uso.
    // Recibe el GrupoAlcoholico (un Value Object), no la Bebida completa:
    // Consumidor y Bebida son agregados distintos y no se conocen entre sí.
    public void verificarPuedeComprar(GrupoAlcoholico grupo, int unidades) {
        if (grupo == null) {
            throw new ReglaDominioException("El grupo alcohólico de la bebida es obligatorio.");
        }
        if (unidades < 1) {
            throw new ReglaDominioException("Debe comprarse al menos una unidad.");
        }
        if (grupo.requiereMayoriaEdad() && !esMayorDeEdad()) {
            throw new ReglaDominioException("Un menor de edad no puede comprar bebidas alcohólicas.");
        }
        if (!puedeComprarPorVolumen(unidades)) {
            throw new ReglaDominioException(
                    "Comprar más de " + UNIDADES_SIN_RESTRICCION
                            + " unidades requiere más de " + COMPRAS_NECESARIAS_PARA_VOLUMEN
                            + " compras históricas.");
        }
    }

    public void registrarCompraExitosa() {
        this.comprasHistoricas++;
    }

    // ---------- Consultas ----------

    public boolean esMayorDeEdad() {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        return edad >= EDAD_MINIMA_PARA_ALCOHOL;
    }

    public boolean puedeComprarPorVolumen(int unidadesDeseadas) {
        if (unidadesDeseadas > UNIDADES_SIN_RESTRICCION) {
            return this.comprasHistoricas > COMPRAS_NECESARIAS_PARA_VOLUMEN;
        }
        return true;
    }

    // Sin setters: solo lectura hacia afuera.

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public CodigoConsumidor getCodigoConsumidor() {
        return codigoConsumidor;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int getComprasHistoricas() {
        return comprasHistoricas;
    }

    // equals/hashCode por id: dos Consumidor son "el mismo" solo si comparten id.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consumidor that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}