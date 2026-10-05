# Reglas de negocio innegociables

**Proyecto:** Vent2Bebidas

Estas reglas **nunca pueden romperse**: el sistema las impide en el dominio, y no dependen de que la interfaz o un caso de uso "se acuerde" de validarlas. Cada regla indica **por qué existe**, **dónde se protege** en el código y **qué prueba** lo demuestra.

---

## Reglas del envase

### RN-01 — Un envase de aluminio nunca puede ser retornable
- **¿Por qué existe?** El aluminio de un solo uso (como las latas) se recicla como material; no se lava ni se rellena. Por eso no entra al ciclo de retorno.
- **Dónde se protege:** `TipoEnvase` (constructor del record) y `MaterialEnvase.permiteRetorno()`.
- **Prueba:** `TipoEnvaseTest.noDebeCrearUnTipoRetornableDeAluminio`.

### RN-02 — Un envase nunca puede superar el máximo de usos de su material
- **¿Por qué existe?** Cada lavado y llenado desgasta el envase. El vidrio resiste más ciclos que el plástico (vidrio 30, plástico 10). Pasado el límite se compromete la higiene y la seguridad.
- **Dónde se protege:** `Envase.registrarRetorno()` y `TipoEnvase.usosMaximos()`.
- **Prueba:** `EnvaseTest.noDebeSuperarElMaximoDeUsosDeSuMaterial`.

### RN-03 — Cada retorno suma exactamente un uso
- **¿Por qué existe?** La cuenta de usos es la base de RN-02. Si se contara mal, el control de seguridad dejaría de ser confiable.
- **Dónde se protege:** `Envase.registrarRetorno()`, que incrementa `cantidadUsos` solo después de validar.
- **Prueba:** `EnvaseTest.debeAumentarUsosYCambiarEstadoAlRetornar`.

### RN-04 — Un envase dañado nunca puede ser retornado; solo puede descartarse
- **¿Por qué existe?** Un envase roto o con fisuras no es seguro para volver a llenarse. Lo único que corresponde es sacarlo de circulación.
- **Dónde se protege:** `Envase.registrarRetorno()` y `EstadoEnvase.puedeTransicionarA()`.
- **Prueba:** `EnvaseTest.noDebeRetornarUnEnvaseDanado`.

### RN-05 — Un envase descartado nunca puede volver a modificarse
- **¿Por qué existe?** El descarte es definitivo. Evita "revivir" envases que ya salieron de circulación y preserva el historial.
- **Dónde se protege:** `Envase.verificarQueNoEstaDescartado()` y `EstadoEnvase.esFinal()`.
- **Prueba:** `EnvaseTest.unEnvaseDescartadoNoPuedeRetornarse`, `descartarDosVecesDebeLanzarExcepcion`.

---

## Reglas de la bebida

### RN-06 — El precio de una bebida siempre es mayor que cero y no puede cambiar de moneda
- **¿Por qué existe?** Una bebida sin un precio válido no puede venderse. Cambiar la moneda cambiaría la oferta completa, no solo el precio.
- **Dónde se protege:** `Precio` (constructor del record) y `Bebida.cambiarPrecio()`.
- **Pruebas:** `PrecioTest.noDebeCrearPrecioEnCero`, `BebidaTest.noDebePermitirCambiarLaMonedaDelPrecio`.

### RN-07 — Una bebida eliminada nunca puede modificarse ni venderse, y nunca se borra
- **¿Por qué existe?** Retirarla del catálogo no debe destruir su historial, que se necesita para compras pasadas y auditoría. Por eso la eliminación es lógica.
- **Dónde se protege:** `Bebida.eliminarLogicamente()`, `verificarQueNoEstaEliminada()` y `verificarQueEstaDisponible()`.
- **Pruebas:** `BebidaTest.noDebePermitirCambiarElPrecioDeUnaBebidaEliminada`, `unaBebidaEliminadaNoPuedeVenderse`.

---

## Reglas del consumidor

### RN-08 — Un menor de edad nunca puede comprar bebidas alcohólicas
- **¿Por qué existe?** La venta de alcohol a menores de 18 años está prohibida. El sistema debe impedirla sin depender de que el vendedor lo verifique.
- **Dónde se protege:** `Consumidor.verificarPuedeComprar()` y `GrupoAlcoholico.requiereMayoriaEdad()`.
- **Pruebas:** `ConsumidorTest.unMenorNoPuedeComprarBebidasAlcoholicas`, `ConfirmarCompraDeBebidaUseCaseTest.noDebeVenderBebidaAlcoholicaAUnMenor`.

### RN-09 — Comprar más de 5 unidades de una vez exige más de 3 compras históricas
- **¿Por qué existe?** Las compras grandes de un consumidor sin historial son un riesgo (reventa o fraude). El historial es la señal de confianza.
- **Dónde se protege:** `Consumidor.verificarPuedeComprar()` y `puedeComprarPorVolumen()`.
- **Pruebas:** `ConsumidorTest.noDebePermitirComprarMasDeCincoUnidadesSinHistorial`, `debePermitirComprarMasDeCincoUnidadesConMasDeTresComprasHistoricas`.

### RN-10 — El código de consumidor es único y tiene exactamente 6 caracteres alfanuméricos
- **¿Por qué existe?** El código identifica al consumidor para acceder al catálogo. Si se repitiera, dos personas compartirían la misma identidad de acceso.
- **Dónde se protege:** el formato, en `CodigoConsumidor`; la unicidad, en `RegistrarConsumidorUseCase`, porque exige comparar contra todos los consumidores.
- **Pruebas:** `CodigoConsumidorTest.noDebeCrearUnCodigoDeLongitudDistintaDeSeis`, `RegistrarConsumidorUseCaseTest.noDebePermitirDosConsumidoresConElMismoCodigo`.

---

## Resumen

| ID | Regla | Agregado |
|---|---|---|
| RN-01 | Un envase de aluminio nunca puede ser retornable | Envase |
| RN-02 | Un envase nunca supera el máximo de usos de su material | Envase |
| RN-03 | Cada retorno suma exactamente un uso | Envase |
| RN-04 | Un envase dañado nunca puede ser retornado | Envase |
| RN-05 | Un envase descartado nunca vuelve a modificarse | Envase |
| RN-06 | El precio es mayor que cero y no cambia de moneda | Bebida |
| RN-07 | Una bebida eliminada no se modifica ni se vende; nunca se borra | Bebida |
| RN-08 | Un menor nunca compra bebidas alcohólicas | Consumidor |
| RN-09 | Más de 5 unidades exige más de 3 compras históricas | Consumidor |
| RN-10 | El código de consumidor es único y de 6 caracteres alfanuméricos | Consumidor |
