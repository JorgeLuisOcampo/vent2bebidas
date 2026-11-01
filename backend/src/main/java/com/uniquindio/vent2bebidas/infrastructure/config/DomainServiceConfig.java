package com.uniquindio.vent2bebidas.infrastructure.config;

import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import com.uniquindio.vent2bebidas.domain.service.VerificadorCodigoConsumidor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Los servicios del dominio NO llevan @Service (el dominio no conoce Spring).
// Aquí, en infraestructura, le decimos a Spring cómo construirlos.
@Configuration
public class DomainServiceConfig {

    @Bean
    public VerificadorCodigoConsumidor verificadorCodigoConsumidor(ConsumidorRepository consumidorRepository) {
        return new VerificadorCodigoConsumidor(consumidorRepository);
    }
}
