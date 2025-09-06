package ar.edu.utn.dds.k3003.repository;
import ar.edu.utn.dds.k3003.model.Solicitud;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Profile("test")
public class InMemorySolicitudRepo implements SolicitudRepository {

    private List<Solicitud> solicitudes;

    public InMemorySolicitudRepo(){
        this.solicitudes = new ArrayList<>();
    }

    @Override
    public Optional<Solicitud> findById(String id) {
        return this.solicitudes.stream().filter(x -> x.getId().equals(id)).findFirst();
    }

    @Override
    public List<Solicitud> findByHechoId(String id){
        return this.solicitudes.stream().filter(x -> Objects.equals(x.getHechoId(), id)).toList();
    }
    /*
    @Override
    public Solicitud save(Solicitud solicitud) {
        this.solicitudes.add(solicitud);
        //hecho.setFechaModificacion(LocalDateTime.now());
        return solicitud;
    }*/

    @Override
    public void delete(String id){
        Optional<Solicitud> solicitudOptional = findById(id);
        if (solicitudOptional.isEmpty()){
            throw  new IllegalArgumentException("La solicitud: " + id +" no existe");
        }
        Solicitud solicitudDelete = solicitudOptional.get();
        this.solicitudes.remove(solicitudDelete);
    }
}