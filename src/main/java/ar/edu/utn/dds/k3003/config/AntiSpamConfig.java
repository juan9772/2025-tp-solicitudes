package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.app.AntiSpamService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AntiSpamConfig {
    @Bean
    public AntiSpamService antiSpamService() {
        return texto -> false; // Mock
    }
}