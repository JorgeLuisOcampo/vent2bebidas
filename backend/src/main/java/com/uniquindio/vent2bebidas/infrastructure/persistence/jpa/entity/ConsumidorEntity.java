package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

// Clase SOLO para la base de datos. Las reglas del consumidor viven en domain.entity.Consumidor.
@Entity
@Table(name = "consumidores")
public class ConsumidorEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "nombre", length = 120, nullable = false)
    private String nombre;

    // El Value Object CodigoConsumidor se guarda como texto. unique = la base también lo protege.
    @Column(name = "codigo", length = 6, nullable = false, unique = true)
    private String codigo;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "compras_historicas", nullable = false)
    private int comprasHistoricas;

    public ConsumidorEntity() {
        // requerido por JPA
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public int getComprasHistoricas() { return comprasHistoricas; }
    public void setComprasHistoricas(int comprasHistoricas) { this.comprasHistoricas = comprasHistoricas; }
}