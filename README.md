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

## Pruebas avanzadas
Todas las pruebas corren con `./gradlew test` (desde la carpeta `backend`).
### 1. Probar un endpoint con seguridad
Probamos la ruta protegida `/api/bebidas` con tres casos: sin token responde 401 (el
sistema no sabe quién eres),
con el rol COMPRADOR intentando publicar responde 403 (sabe quién eres pero no tienes
permiso) y con un
usuario autenticado viendo el catálogo responde 200. Usamos `@SpringBootTest` con
`@AutoConfigureMockMvc` para
que se cargue nuestra seguridad real, y `jwt()` de `spring-security-test` para simular un
usuario con el rol
que queramos sin pasar por el login. Un `@WebMvcTest` no carga nuestro `SecurityConfig`,
por eso ahí apagamos
los filtros con `addFilters = false`. 

Lo que más nos costó: [escribe aquí tu experiencia].
### 2. @DataJpaTest contra @SpringBootTest
`@SpringBootTest` levanta toda la aplicación; `@DataJpaTest` levanta solo la capa de
persistencia (entidades y repositorios de Spring Data), así que es más rápida y 
deshace cada prueba sola. Como no carga nuestros
`@Repository` ni `@Component`, tuvimos que traer el adaptador y el mapper con
`@Import`. En Spring Boot 4 el
paquete de `@DataJpaTest` cambió a
`org.springframework.boot.data.jpa.test.autoconfigure` y necesita el starter
de pruebas `spring-boot-starter-data-jpa-test`; en nuestro caso ya estaba en el
`build.gradle`.


Lo que más nos costó: [escribe aquí tu experiencia].
### 3. Prueba de integración
Una prueba de integración recorre varias piezas funcionando juntas de verdad: la petición
entra por el
controller, pasa por el caso de uso, el repositorio JPA y llega a la base H2, sin ningún
`@MockitoBean`.
En cambio, una prueba unitaria prueba una sola clase y simula todo lo demás con Mockito,
por eso es más rápida,
pero no garantiza que las piezas funcionen juntas. Nuestra prueba publica una bebida y
luego la consulta, y
cambia el precio de una bebida de `data.sql`. Conviene usarlas para los flujos importantes y
dejar los casos de borde para las unitarias. 

Lo que más nos costó: [escribe aquí tu experiencia].