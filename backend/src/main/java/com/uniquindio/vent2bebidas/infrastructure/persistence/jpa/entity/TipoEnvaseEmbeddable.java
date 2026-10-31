package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity;

import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

// El Value Object TipoEnvase se guarda en TRES columnas (la capacidad se guarda como int de mililitros).
@Embeddable
public class TipoEnvaseEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_material", nullable = false, length = 20)
    private MaterialEnvase material;

    @Column(name = "tipo_capacidad_ml", nullable = false)
    private int capacidadMl;

    @Column(name = "tipo_retornable", nullable = false)
    private boolean retornable;

    protected TipoEnvaseEmbeddable() {
        // requerido por JPA
    }

    public TipoEnvaseEmbeddable(MaterialEnvase material, int capacidadMl, boolean retornable) {
        this.material = material;
        this.capacidadMl = capacidadMl;
        this.retornable = retornable;
    }

    public MaterialEnvase getMaterial() { return material; }
    public void setMaterial(MaterialEnvase material) { this.material = material; }
    public int getCapacidadMl() { return capacidadMl; }
    public void setCapacidadMl(int capacidadMl) { this.capacidadMl = capacidadMl; }
    public boolean isRetornable() { return retornable; }
    public void setRetornable(boolean retornable) { this.retornable = retornable; }
}