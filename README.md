# Vent2Bebidas

Marketplace de bebidas con **envases retornables**. Los vendedores publican sus
bebidas y los consumidores las compran; el sistema controla además el ciclo de
vida de cada envase (cuántas veces se ha reutilizado, si está dañado o descartado).

Proyecto de **Programación Avanzada 2026-2** — Universidad del Quindío.

## Arquitectura

Arquitectura hexagonal con tres capas dentro de `backend/`:

| Capa | Responsabilidad |
|---|---|
| `domain` | Entidades, Value Objects, reglas de negocio e interfaces de Repository. No depende de Spring. |
| `application` | Casos de uso (orquestan el dominio) y DTOs de entrada y salida. |
| `infrastructure` | Adaptadores: repositorios en memoria (más adelante JPA y REST). |

## Modelo del dominio

Tres agregados, cada uno con su propia raíz:

- **Envase**: ciclo de vida de un envase retornable (nuevo, retornado, dañado, descartado).
- **Bebida**: producto del vendedor, con precio y borrado lógico.
- **Consumidor**: comprador, con reglas de edad y de volumen de compra.

Value Objects: `Capacidad`, `MaterialEnvase`, `TipoEnvase`, `EstadoEnvase`,
`GrupoAlcoholico`, `Precio`, `CodigoConsumidor`, `EstadoBebida`.

## Reglas de negocio destacadas

- Un envase de aluminio nunca puede ser retornable.
- Un envase no puede superar el máximo de usos de su material (vidrio 30, plástico 10).
- Un envase dañado solo puede descartarse; uno descartado ya no cambia.
- Un menor de edad no puede comprar bebidas alcohólicas.
- Comprar más de 5 unidades de una vez exige más de 3 compras históricas.
- Una bebida eliminada no puede modificarse ni venderse (borrado lógico).


## Documentación

- `glosario-lenguaje-ubicuo.md`: términos del negocio y su uso en el código.
- `docs/`: diagramas y documentos de diseño del dominio.

## Convenciones de trabajo

- Mensajes de commit con prefijo: `feat`, `fix`, `refactor`, `test`, `docs`, `build`.
- Todo cambio entra a `main` por Pull Request.