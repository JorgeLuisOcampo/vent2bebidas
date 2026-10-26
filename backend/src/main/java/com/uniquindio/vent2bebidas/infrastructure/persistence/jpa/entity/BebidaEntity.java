package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity;

import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Esta clase es solo para la BASE DE DATOS. Las reglas de negocio viven en la clase Bebida del dominio.
@Entity
@Table(name = "bebidas")
public class BebidaEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "vendedor_id", length = 36, nullable = false)
    private String vendedorId;

    @Column(name = "nombre", length = 120, nullable = false)
    private String nombre;

    @Embedded
    private PrecioEmbeddable precio;

    @Embedded
    private TipoEnvaseEmbeddable tipoEnvase;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_alcoholico", length = 20, nullable = false)
    private GrupoAlcoholico grupoAlcoholico;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoBebida estado;

    public BebidaEntity() {
        // requerido por JPA
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVendedorId() { return vendedorId; }
    public void setVendedorId(String vendedorId) { this.vendedorId = vendedorId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public PrecioEmbeddable getPrecio() { return precio; }
    public void setPrecio(PrecioEmbeddable precio) { this.precio = precio; }
    public TipoEnvaseEmbeddable getTipoEnvase() { return tipoEnvase; }
    public void setTipoEnvase(TipoEnvaseEmbeddable tipoEnvase) { this.tipoEnvase = tipoEnvase; }
    public GrupoAlcoholico getGrupoAlcoholico() { return grupoAlcoholico; }
    public void setGrupoAlcoholico(GrupoAlcoholico grupoAlcoholico) { this.grupoAlcoholico = grupoAlcoholico; }
    public EstadoBebida getEstado() { return estado; }
    public void setEstado(EstadoBebida estado) { this.estado = estado; }
}