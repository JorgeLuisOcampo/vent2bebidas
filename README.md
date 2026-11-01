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
| `infrastructure` | Adaptadores: persistencia (JPA para `Bebida`, en memoria para `Envase` y `Consumidor`), API REST y seguridad JWT. |

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

## API de bebidas

La API usa tokens JWT. Los roles viajan en el claim `roles` del token (`COMPRADOR` y `VENDEDOR`)
y el identificador del usuario en el claim `uid`.

| Método y ruta | Qué hace | Quién puede |
|---|---|---|
| `GET /api/bebidas` | Busca bebidas con paginación (`texto`, `grupo`, `pagina`, `tamano`) | `COMPRADOR` o `VENDEDOR` |
| `GET /api/bebidas/{id}` | Detalle de una bebida | `COMPRADOR` o `VENDEDOR` |
| `POST /api/bebidas` | Publica una bebida (el vendedor sale del claim `uid`) | `VENDEDOR` |
| `PUT /api/bebidas/{id}/precio` | Cambia el precio de una bebida | `VENDEDOR` |

Los errores de regla de negocio responden 400 y los recursos que no existen responden 404.

## Ejecutar las pruebas

```bash
cd backend
./gradlew test          # Linux / macOS / Git Bash
gradlew.bat test        # Windows (cmd o PowerShell)
```

## Pruebas avanzadas

Todas estas pruebas corren y pasan con `./gradlew test`.

### 1. Probar un endpoint con seguridad

En el punto 1 probamos que la ruta protegida `/api/bebidas` responda distinto según quién la llame.
Hicimos tres pruebas: sin token responde 401, con el rol COMPRADOR intentando publicar una bebida
responde 403 y con el rol COMPRADOR viendo el catálogo responde 200. El 401 significa que el sistema no
sabe quién eres, y el 403 que sí lo sabe pero no tienes permiso para esa acción. Usamos `@SpringBootTest`
con `@AutoConfigureMockMvc` para que se cargue nuestra `SecurityConfig` real, y el `jwt()` de
`spring-security-test` para simular un usuario autenticado con el rol que queramos, sin pasar por un login.
Un `@WebMvcTest` no carga esa configuración, por eso en las pruebas del controller apagamos los filtros.
Lo que más nos costó fue entender por qué la misma ruta `/api/bebidas` da 200 con GET y 403 con POST.

### 2. @DataJpaTest contra @SpringBootTest

`@SpringBootTest` levanta toda la aplicación (controllers, casos de uso, seguridad y base de datos),
mientras que `@DataJpaTest` levanta solo la capa de persistencia: las entidades y los repositorios de
Spring Data. Por eso es más rápida y además deshace cada prueba sola. A cambio, no carga nuestros
`@Repository` ni `@Component`, así que tuvimos que traer el adaptador y el mapper con `@Import`.
En Spring Boot 4 esta anotación cambió de paquete (`org.springframework.boot.data.jpa.test.autoconfigure`)
y necesita su propio starter de pruebas, `spring-boot-starter-data-jpa-test`; en nuestro caso ya estaba
en el `build.gradle`, solo tuvimos que importar bien la anotación. Lo que más nos costó fue que el IDE
no encontraba el método `buscar` del repositorio: nos faltaba el commit que lo agregaba.

### 3. Prueba de integración

Una prueba de integración recorre varias piezas funcionando juntas de verdad, sin simular nada. La nuestra
hace una petición HTTP que pasa por la seguridad, el controller, el caso de uso y el repositorio JPA, y
llega hasta la base de datos H2: publica una bebida y luego la consulta, y también cambia el precio de una
bebida de `data.sql` y comprueba que el cambio quedó guardado. Una prueba unitaria, en cambio, prueba una
sola clase y simula todo lo demás con Mockito; es más rápida, pero no nos asegura que las piezas funcionen
juntas. Conviene usar la integración para los flujos importantes y dejar los casos de borde para las unitarias.
Lo que más nos costó fue entender que la petición pasa por dos mappers distintos: el de la API y el de persistencia.

## Documentación

- `glosario-lenguaje-ubicuo.md`: términos del negocio y su uso en el código.
- `docs/`: diagramas y documentos de diseño del dominio.

## Convenciones de trabajo

- Mensajes de commit con prefijo: `feat`, `fix`, `refactor`, `test`, `docs`, `build`.
- Todo cambio entra a `main` por Pull Request.
