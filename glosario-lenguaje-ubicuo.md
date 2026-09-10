# Glosario del Lenguaje Ubicuo - [vent2bebidas / ]

## Nombres Juan David Torres Arango - Oscar Leando Agudelo Franco - Jorge Luis Ocampo Ocampo
            
            

## Conceptos Centrales

### [Envase ]
**Definición:** Contenedor del producto de venta

**Sinónimos aceptados:** [si aplica]
**No usar:** [botella ]

**Ejemplo de uso en código:**
\`\`\`java
envase.obtenerTipo()
\`\`\`

---

### [Envase retornable]
**Definición:** [Envase vacio de ciertas caracteristicas que puede ser retornado]
**No usar:** [botella retornable]

**Precondiciones:** [cumplir las caracteristicas]

**Ejemplo de uso:**
\`\`\`java
envase.esRetornable()
\`\`\`

---

### [Consumidor ]
**Definición:** [Persona natural o juridica registrado en el sistema]
**No usar:** [persona ]

**Precondiciones:** [estar registrado]

**Ejemplo de uso:**

consumidor.solicitudDevolucion()

---

### [grupoAlcoholico ]
**Definición:** [Delimita las caracteristicas de las bebidas en base a su preparacion]
**No usar:** [categoria ]

**Precondiciones:** [contenido de alcohol]

**Ejemplo de uso:**

cerveza.grupoAlcoholico()

---

### [Permiso ]
**Definición:** Solicitud de permiso para venta de bebida artesanal
**No usar:** [Peticion ]

**Precondiciones:** [estar registrado]

**Ejemplo de uso:**
revision.tramite(Permiso)
---

### [Tramite ]
**Definición:** Gestion de diferentes proceso internos relacionados a la actividad comercial
**No usar:** [Diligencia ]

**Precondiciones:** [estar registrado]

**Ejemplo de uso:** 
tramite.pagar()

---

## Anti-patrones (Términos a EVITAR en nuestro proyecto)

| No usar | Usar |
|---|---|
| [categoria] | [grupoAlcoholico]
| [botella] | [Envase] |
| [Botella retornable] | [Envase retornable] |
| [Persona] | [Consumidor] |
| [Peticion] | [Permiso] |
| [Diligencia] | [Tramite] |