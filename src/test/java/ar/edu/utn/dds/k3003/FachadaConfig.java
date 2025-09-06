package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.app.AntiSpamService;
import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.repository.JpaSolicitudRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
class FachadaConfig {
    @Bean
    public Fachada fachadaSolicitudes(JpaSolicitudRepository repo,
                                      FachadaFuente fachadaFuente,
                                      AntiSpamService antiSpam) {
        Fachada f = new Fachada(repo);
        f.setFachadaFuente(fachadaFuente);
        f.setAntiSpam(antiSpam);
        return f;
    }
}
