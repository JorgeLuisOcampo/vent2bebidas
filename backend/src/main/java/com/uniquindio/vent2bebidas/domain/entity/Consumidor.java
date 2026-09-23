package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoComprador;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.UUID;

public class Consumidor {

    private final UUID id;
    private final String nombre;
    private final CodigoComprador codigoComprador;
    private final LocalDate fechaNacimiento;
    private int comprasHistoricas;

    public Consumidor(UUID id, String nombre, CodigoComprador codigoComprador, LocalDate fechaNacimiento, int comprasHistoricas){

        if (id == null){
            throw new ReglaDominioException("El id del consumidor debe ser obligatorio.");
        }

        if (nombre == null){
            throw new ReglaDominioException("El nombre del consumidor debe ser obligatorio.");
        }

        if (codigoComprador == null){
            throw new ReglaDominioException("Debe tener un código válido para acceder al catálogo.");
        }

        if(fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())){
            throw new ReglaDominioException("La fecha de nacimiento no es válida.");
        }

        if(comprasHistoricas < 0){
            throw new ReglaDominioException("El historial de compras no puede ser negativo.");
        }

        this.id = id;
        this.nombre = nombre;
        this.codigoComprador = codigoComprador;
        this.fechaNacimiento = fechaNacimiento;
        this.comprasHistoricas = comprasHistoricas;
    }

    public UUID getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public CodigoComprador getCodigoComprador() {
        return codigoComprador;
    }
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    public int getComprasHistoricas() {
        return comprasHistoricas;
    }

    public boolean esMayorDeEdad(){
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        return edad >= 18;
    }

    public boolean puedeComprarPorVolumen(int unidadesDeseadas){
        if (unidadesDeseadas > 5){
            return this.comprasHistoricas > 3;
        }
        return true;
    }

    public void registrarCompraExitosa(){
        this.comprasHistoricas++;
    }

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
