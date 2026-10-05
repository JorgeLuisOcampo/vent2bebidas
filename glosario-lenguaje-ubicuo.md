# Glosario del Lenguaje Ubicuo - Vent2Bebidas

**Integrantes:** Juan David Torres Arango · Oscar Leandro Agudelo Franco · Jorge Luis Ocampo Ocampo

## Conceptos Centrales

### Envase
**Definición:** Objeto físico individual que contiene una bebida. Cada envase tiene su propia identidad, su propio estado (nuevo, retornado, dañado, descartado) y su propio historial de usos. Dos envases del mismo tipo siguen siendo dos envases distintos.

**Sinónimos aceptados:** ninguno
**No usar:** botella, frasco, recipiente

**Ejemplo de uso en código:**
```java
Envase envase = Envase.registrar(UUID.randomUUID(), tipo);
```

---

### Envase retornable
**Definición:** Envase cuyo tipo permite volver a circulación después de ser devuelto, para lavarse y rellenarse. Que un envase sea retornable depende de su material: el vidrio y el plástico lo permiten; el aluminio no.

**Precondiciones:** Su material debe permitir el retorno. Un envase de aluminio nunca puede ser retornable.

**No usar:** botella retornable, envase reutilizable

**Ejemplo de uso en código:**
```java
TipoEnvase tipo = new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);
```

---

### Retorno
**Definición:** Acción de devolver un envase retornable para que vuelva a circulación. Cada retorno suma exactamente un uso al envase y lo deja en estado `RETORNADO`.

**Precondiciones:**
- El envase debe ser retornable.
- El envase no debe estar dañado ni descartado.
- El envase no debe haber alcanzado el máximo de usos de su material.

**Sinónimos aceptados:** ninguno
**No usar:** devolución, devolver, regreso

**Ejemplo de uso:**
```java
envase.registrarRetorno();
```

---

### Uso
**Definición:** Cada vez que un envase retornable vuelve a circulación cuenta como un uso. Cada material tiene un máximo de usos: vidrio 30 y plástico 10. Al llegar al máximo, el envase debe descartarse.

**Precondiciones:** La cantidad de usos nunca supera el máximo del material del envase.

**No usar:** ciclo, vuelta, reutilización

**Ejemplo de uso:**
```java
int quedan = envase.usosRestantes();
```

---

### Descarte
**Definición:** Salida definitiva de un envase de circulación, porque se dañó o porque agotó sus usos. Un envase descartado queda en estado final: ya no puede modificarse ni retornarse. Los envases se **descartan**; las bebidas se **eliminan lógicamente**.

**Precondiciones:** El envase no debe estar ya descartado.

**No usar:** botar, desechar, borrar envase

**Ejemplo de uso:**
```java
envase.descartar();
```

---

### Grupo alcohólico
**Definición:** Clasificación de una bebida según su contenido de alcohol: `SIN_ALCOHOL`, `FERMENTADA` o `DESTILADA`. Determina si la bebida exige mayoría de edad para comprarla.

**Precondiciones:** Toda bebida debe pertenecer a un grupo alcohólico.

**No usar:** categoría, tipo de bebida

**Ejemplo de uso:**
```java
boolean exigeMayoria = bebida.getGrupoAlcoholico().requiereMayoriaEdad();
```

---

### Publicar
**Definición:** Acción del vendedor de poner una bebida a disposición de los consumidores en el catálogo. Toda bebida nace publicada.

**Precondiciones:** La bebida debe tener nombre, tipo de envase, grupo alcohólico, precio mayor que cero y un vendedor.

**No usar:** crear bebida, agregar producto, subir

**Ejemplo de uso:**
```java
Bebida bebida = Bebida.publicar(UUID.randomUUID(), vendedorId, "Cerveza Artesanal",
        tipo, GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
```

---

### Eliminación lógica
**Definición:** Retirar una bebida del catálogo sin borrarla del sistema. La bebida queda en estado `ELIMINADA`: no puede modificarse ni venderse, pero su historial se conserva.

**Precondiciones:** La bebida no debe estar ya eliminada.

**No usar:** borrar, delete, quitar

**Ejemplo de uso:**
```java
bebida.eliminarLogicamente();
```

---

### Consumidor
**Definición:** Persona natural registrada en el sistema que adquiere bebidas. Su edad y su historial de compras determinan qué puede comprar: un menor de edad no puede comprar bebidas alcohólicas, y las compras grandes exigen historial.

**Precondiciones:** Estar registrado con nombre, código de consumidor válido y fecha de nacimiento no futura.

**No usar:** comprador, cliente, usuario, persona

**Ejemplo de uso:**
```java
Consumidor consumidor = Consumidor.registrar(UUID.randomUUID(), "Ana Gómez",
        new CodigoConsumidor("AB12CD"), LocalDate.of(1995, 4, 12));
```

---

### Compra por volumen
**Definición:** Compra de más de 5 unidades de una vez. Solo la puede hacer un consumidor con más de 3 compras exitosas en su historial.

**Precondiciones:** El consumidor debe tener más de 3 compras históricas.

**No usar:** compra mayorista, compra grande

**Ejemplo de uso:**
```java
boolean permitida = consumidor.puedeComprarPorVolumen(6);
```

---

## Anti-patrones

| No usar | Usar |
|---|---|
| botella, frasco | Envase |
| botella retornable | Envase retornable |
| devolución, devolver | Retorno (`registrarRetorno`) |
| ciclo, vuelta, reutilización | Uso |
| botar, desechar un envase | Descarte (`descartar`) |
| categoría, tipo de bebida | Grupo alcohólico |
| crear bebida, agregar producto | Publicar (`Bebida.publicar`) |
| borrar bebida, delete | Eliminación lógica (`eliminarLogicamente`) |
| comprador, cliente, usuario, persona | Consumidor |
| compra mayorista, compra grande | Compra por volumen |
