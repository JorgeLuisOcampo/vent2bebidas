# Agregados e invariantes

**Proyecto:** Vent2Bebidas

---

## 1. Los tres agregados

| Agregado | Raíz | Qué representa | Repository |
|---|---|---|---|
| **Envase** | `Envase` | Un envase físico retornable y su ciclo de vida | `EnvaseRepository` |
| **Bebida** | `Bebida` | El producto que publica un vendedor | `BebidaRepository` |
| **Consumidor** | `Consumidor` | El comprador, con su edad e historial | `ConsumidorRepository` |

**¿Por qué son tres y no uno solo?** Porque cambian de forma independiente: se puede retornar un envase sin tocar ninguna bebida, cambiar un precio sin tocar ningún envase, y comprar sin modificar la bebida. Además, cada uno protege reglas distintas.

---

## 2. Agregado Envase

- **Dentro del límite:** `Envase` (raíz), `TipoEnvase`, `Capacidad`, `MaterialEnvase`, `EstadoEnvase`
- **Fuera del límite:** `Bebida`, `Consumidor`, `Vendedor`

| ID | Invariante | Dónde se protege | Prueba |
|---|---|---|---|
| E1 | Un envase **nunca** retorna si su tipo no es retornable | `Envase.registrarRetorno()` | `EnvaseTest.noDebeRetornarEnvaseNoRetornable` |
| E2 | Un envase dañado **nunca** puede retornarse | `Envase.registrarRetorno()` | `EnvaseTest.noDebeRetornarUnEnvaseDanado` |
| E3 | Un envase descartado **nunca** vuelve a modificarse | `Envase.verificarQueNoEstaDescartado()` | `EnvaseTest.unEnvaseDescartadoNoPuedeRetornarse` |
| E4 | Cada retorno suma **exactamente 1** uso | `Envase.registrarRetorno()` | `EnvaseTest.debeAumentarUsosYCambiarEstadoAlRetornar` |
| E5 | Un envase **nunca** supera el máximo de usos de su material | `Envase.registrarRetorno()` | `EnvaseTest.noDebeSuperarElMaximoDeUsosDeSuMaterial` |
| E6 | Todo envase **siempre** nace `NUEVO` con 0 usos | `Envase.registrar()` | `EnvaseTest.registrarCreaUnEnvaseNuevoConCeroUsos` |

---

## 3. Agregado Bebida

- **Dentro del límite:** `Bebida` (raíz), `Precio`, `TipoEnvase`, `EstadoBebida`, `GrupoAlcoholico`
- **Fuera del límite:** `Vendedor` (solo se guarda `vendedorId`), `Envase`, `Consumidor`

| ID | Invariante | Dónde se protege | Prueba |
|---|---|---|---|
| B1 | Toda bebida **siempre** tiene nombre, vendedor, envase, grupo y precio | `Bebida.publicar()` | `BebidaTest.noDebePermitirPublicarUnaBebidaSinNombre` |
| B2 | El precio **nunca** es cero ni negativo | `Precio` | `PrecioTest.noDebeCrearPrecioEnCero` |
| B3 | Un cambio de precio **siempre** debe dar un valor distinto | `Bebida.cambiarPrecio()` | `BebidaTest.noDebePermitirCambiarElPrecioAlMismoValorActual` |
| B4 | El precio **nunca** cambia de moneda | `Bebida.cambiarPrecio()` | `BebidaTest.noDebePermitirCambiarLaMonedaDelPrecio` |
| B5 | Una bebida eliminada **nunca** puede modificarse | `Bebida.verificarQueNoEstaEliminada()` | `BebidaTest.noDebePermitirCambiarElPrecioDeUnaBebidaEliminada` |
| B6 | Una bebida eliminada **nunca** puede venderse | `Bebida.verificarQueEstaDisponible()` | `BebidaTest.unaBebidaEliminadaNoPuedeVenderse` |
| B7 | Una bebida **solo se elimina una vez** y de forma lógica | `Bebida.eliminarLogicamente()` | `BebidaTest.eliminarLogicamenteDosVecesDebeLanzarExcepcion` |

---

## 4. Agregado Consumidor

- **Dentro del límite:** `Consumidor` (raíz), `CodigoConsumidor`
- **Fuera del límite:** `Bebida`, `Envase`

| ID | Invariante | Dónde se protege | Prueba |
|---|---|---|---|
| C1 | Todo consumidor **siempre** tiene nombre, código y fecha de nacimiento no futura | `Consumidor.registrar()` | `ConsumidorTest.noDebePermitirRegistrarConFechaDeNacimientoFutura` |
| C2 | Un menor de edad **nunca** compra bebidas alcohólicas | `Consumidor.verificarPuedeComprar()` | `ConsumidorTest.unMenorNoPuedeComprarBebidasAlcoholicas` |
| C3 | Comprar más de 5 unidades **siempre** exige más de 3 compras previas | `Consumidor.verificarPuedeComprar()` | `ConsumidorTest.noDebePermitirComprarMasDeCincoUnidadesSinHistorial` |
| C4 | Toda compra **debe** ser de al menos 1 unidad | `Consumidor.verificarPuedeComprar()` | `ConsumidorTest.noDebePermitirComprarCeroUnidades` |
| C5 | El historial de compras **nunca** es negativo y crece de 1 en 1 | `Consumidor.registrarCompraExitosa()` | `ConsumidorTest.registrarCompraExitosaSumaExactamenteUnaCompra` |

**Una regla que cruza agregados:** el código de consumidor nunca puede repetirse. No puede vivir en `Consumidor`, porque para saber si ya existe hay que mirar a todos los consumidores. Por eso se valida en `RegistrarConsumidorUseCase`.

---

## 5. Cómo se comunican los agregados

- **Por id:** `Bebida` guarda `vendedorId`, no un objeto `Vendedor`.
- **Por valor:** `Consumidor.verificarPuedeComprar(GrupoAlcoholico, int)` recibe un valor, no una `Bebida`.
- **Con un caso de uso:** `ConfirmarCompraDeBebidaUseCase` **lee** la `Bebida` y **modifica solo** el `Consumidor`.
