package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.model.Solicitud;

import java.util.List;
import java.util.Optional;

public interface SolicitudRepository {

    Optional<Solicitud> findById(String id);
    List<Solicitud> findByHechoId(String id);
    //Solicitud save(Solicitud solicitud);
    void delete(String id);
}
