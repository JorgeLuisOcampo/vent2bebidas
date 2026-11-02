package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity;

import com.uniquindio.vent2bebidas.domain.valueobject.EstadoCompra;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// Clase SOLO para la base de datos. Las reglas de la compra viven en domain.entity.Compra.
// Las otras tablas se referencian por id (igual que en el dominio, entre agregados solo hay ids).
@Entity
@Table(name = "compras")
public class CompraEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "consumidor_id", length = 36, nullable = false)
    private String consumidorId;

    @Column(name = "bebida_id", length = 36, nullable = false)
    private String bebidaId;

    @Column(name = "vendedor_id", length = 36, nullable = false)
    private String vendedorId;

    @Column(name = "unidades", nullable = false)
    private int unidades;

    // Precio UNITARIO al momento de la compra (columnas precio_monto y precio_moneda).
    @Embedded
    private PrecioEmbeddable precioUnitario;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoCompra estado;

    public CompraEntity() {
        // requerido por JPA
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConsumidorId() { return consumidorId; }
    public void setConsumidorId(String consumidorId) { this.consumidorId = consumidorId; }
    public String getBebidaId() { return bebidaId; }
    public void setBebidaId(String bebidaId) { this.bebidaId = bebidaId; }
    public String getVendedorId() { return vendedorId; }
    public void setVendedorId(String vendedorId) { this.vendedorId = vendedorId; }
    public int getUnidades() { return unidades; }
    public void setUnidades(int unidades) { this.unidades = unidades; }
    public PrecioEmbeddable getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(PrecioEmbeddable precioUnitario) { this.precioUnitario = precioUnitario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public EstadoCompra getEstado() { return estado; }
    public void setEstado(EstadoCompra estado) { this.estado = estado; }
}