package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity;

import com.uniquindio.vent2bebidas.domain.valueobject.EstadoEnvase;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Clase SOLO para la base de datos. Las reglas del envase viven en domain.entity.Envase.
// Un envase DESCARTADO nunca se borra: queda la fila con ese estado (borrado lógico).
@Entity
@Table(name = "envases")
public class EnvaseEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    // Se reutiliza el mismo @Embeddable que usa la bebida para el Value Object TipoEnvase.
    @Embedded
    private TipoEnvaseEmbeddable tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoEnvase estado;

    @Column(name = "cantidad_usos", nullable = false)
    private int cantidadUsos;

    public EnvaseEntity() {
        // requerido por JPA
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public TipoEnvaseEmbeddable getTipo() { return tipo; }
    public void setTipo(TipoEnvaseEmbeddable tipo) { this.tipo = tipo; }
    public EstadoEnvase getEstado() { return estado; }
    public void setEstado(EstadoEnvase estado) { this.estado = estado; }
    public int getCantidadUsos() { return cantidadUsos; }
    public void setCantidadUsos(int cantidadUsos) { this.cantidadUsos = cantidadUsos; }
}