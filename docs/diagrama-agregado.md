## REGLAS DE NEGOCIO

- Un envase sólo puede utilizar el método `registrarRetorno()` si el **TipoEnvase** es permitido su retorno y su **EstadoEnvase** no es DANADO ni DESCARTADO. 

- Al utilizar `marcarComoDanado()` en un envase, este debe pasar al estado **DANADO**.

- Cada vez que se ejecuta el método `registrarRetorno()`, la cantidad de usos debe aumentar sólamente en 1 y su estado debe actualizarse a **RETORNADO**.