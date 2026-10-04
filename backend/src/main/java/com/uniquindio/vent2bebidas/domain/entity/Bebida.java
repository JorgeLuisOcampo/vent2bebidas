package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;

import java.util.Objects;
import java.util.UUID;


public class Bebida {

    private final UUID id;
    private final UUID vendedorId;
    private final String nombre;
    private final TipoEnvase tipoEnvase;
    private final GrupoAlcoholico grupoAlcoholico;
    private Precio precio;
    private EstadoBebida estado;

    private Bebida(UUID id, UUID vendedorId, String nombre, TipoEnvase tipoEnvase,
                   GrupoAlcoholico grupoAlcoholico, Precio precio) {
        this.id = id;
        this.vendedorId = vendedorId;
        this.nombre = nombre;
        this.tipoEnvase = tipoEnvase;
        this.grupoAlcoholico = grupoAlcoholico;
        this.precio = precio;
        this.estado = EstadoBebida.PUBLICADA;
    }


    public static Bebida publicar(UUID id, UUID vendedorId, String nombre, TipoEnvase tipoEnvase,
                                  GrupoAlcoholico grupoAlcoholico, Precio precio) {
        if (id == null) {
            throw new ReglaDominioException("El identificador de la bebida es obligatorio.");
        }
        if (vendedorId == null) {
            throw new ReglaDominioException("La bebida debe pertenecer a un vendedor.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El nombre de la bebida es obligatorio.");
        }
        if (tipoEnvase == null) {
            throw new ReglaDominioException("El tipo de envase es obligatorio.");
        }
        if (grupoAlcoholico == null) {
            throw new ReglaDominioException("El grupo alcohólico es obligatorio.");
        }
        if (precio == null) {
            throw new ReglaDominioException("El precio de la bebida es obligatorio.");
        }
        return new Bebida(id, vendedorId, nombre.trim(), tipoEnvase, grupoAlcoholico, precio);
    }


    public void cambiarPrecio(Precio nuevoPrecio) {
        verificarQueNoEstaEliminada();
        if (nuevoPrecio == null) {
            throw new ReglaDominioException("El nuevo precio es obligatorio.");
        }
        if (!nuevoPrecio.tieneLaMismaMonedaQue(this.precio)) {
            throw new ReglaDominioException("El precio no puede cambiar de moneda.");
        }
        if (nuevoPrecio.equals(this.precio)) {
            throw new ReglaDominioException("El nuevo precio debe ser diferente al actual.");
        }
        this.precio = nuevoPrecio;
    }

    public void eliminarLogicamente() {
        if (this.estado.esFinal()) {
            throw new ReglaDominioException("La bebida ya está eliminada.");
        }
        this.estado = EstadoBebida.ELIMINADA;
    }


    public boolean estaDisponible() {
        return this.estado == EstadoBebida.PUBLICADA;
    }

    public void verificarQueEstaDisponible() {
        if (!estaDisponible()) {
            throw new ReglaDominioException("La bebida no está disponible para la venta.");
        }
    }


    private void verificarQueNoEstaEliminada() {
        if (this.estado.esFinal()) {
            throw new ReglaDominioException("Una bebida eliminada no puede modificarse.");
        }
    }


    public UUID getId() { return id; }
    public UUID getVendedorId() { return vendedorId; }
    public String getNombre() { return nombre; }
    public TipoEnvase getTipoEnvase() { return tipoEnvase; }
    public GrupoAlcoholico getGrupoAlcoholico() { return grupoAlcoholico; }
    public Precio getPrecio() { return precio; }
    public EstadoBebida getEstado() { return estado; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bebida otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}