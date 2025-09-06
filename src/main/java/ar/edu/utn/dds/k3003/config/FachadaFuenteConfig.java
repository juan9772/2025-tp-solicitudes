package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.app.Fuente;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FachadaFuenteConfig {
    @Bean
    public FachadaFuente fachadaFuente() {
        return new Fuente(); // si usás Mockito
    }

}