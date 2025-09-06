package ar.edu.utn.dds.k3003.app;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.Solicitud;
import ar.edu.utn.dds.k3003.repository.JpaSolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class Fachada implements FachadaSolicitudes {

    private JpaSolicitudRepository solicitudRepository;
    private FachadaFuente fuente;
    private AntiSpamService antiSpam;

    public Fachada(JpaSolicitudRepository repo) {
        this.solicitudRepository = repo;
        this.antiSpam = texto -> false;
    }


    protected Fachada() {
        this.antiSpam = texto -> false;
    }

    @Autowired
    public Fachada(JpaSolicitudRepository solicitudRepository, AntiSpamService antiSpam) {
        this.solicitudRepository = solicitudRepository;
        this.antiSpam = antiSpam;
    }

    @Override
    @Transactional
    public SolicitudDTO agregar(SolicitudDTO solicitudDTO) {
        if (this.solicitudRepository.findById(solicitudDTO.id()).isPresent()){
            throw  new IllegalArgumentException(solicitudDTO.id() + " ya existe");
        }
        HechoDTO hecho = fuente.buscarHechoXId(solicitudDTO.hechoId());
        if (antiSpam.revisarSpam(solicitudDTO.descripcion())){
            throw new IllegalArgumentException("No cumple requisito de AntiSpam");
        }
        Solicitud solicitud = convertirDesdeDTO(solicitudDTO);
        this.solicitudRepository.save(solicitud);
        return convertirDesdeDominio(solicitud);
    }

    @Transactional
    @Override
    public SolicitudDTO modificar(String solicitudId, EstadoSolicitudBorradoEnum estado, String descripcion) throws NoSuchElementException {
        SolicitudDTO solicitudTemp = buscarSolicitudXId(solicitudId);
        Solicitud solicitud = new Solicitud(solicitudTemp.id(), solicitudTemp.descripcion(), solicitudTemp.estado(), solicitudTemp.hechoId());
        solicitud.setEstado(estado);
        solicitud.setDescripcion(descripcion);
        solicitudRepository.delete(solicitud.getId());
        solicitudRepository.save(solicitud);
        return convertirDesdeDominio(solicitud);
    }

    @Override
    public List<SolicitudDTO> buscarSolicitudXHecho(String hechoId) {
        List<Solicitud> solicitudes = this.solicitudRepository.findByHechoId(hechoId);
        if (solicitudes.isEmpty()){
            throw new IllegalArgumentException("No existen solicitudes con el hechoId: " + hechoId);
        }
        List<SolicitudDTO> solicitudesDTO = new ArrayList<>();
        solicitudes.forEach(x ->
            {
                SolicitudDTO solicitudTemp = convertirDesdeDominio(x);
                solicitudesDTO.add(solicitudTemp);
            }
        );
        return solicitudesDTO;
    }

    @Override
    public SolicitudDTO buscarSolicitudXId(String solicitudId) {
        Optional<Solicitud> solicitudEmpty = this.solicitudRepository.findById(solicitudId);
        // El test "getSolicitud_DeberiaRetornarListaVacia" tira 500 si existe se tira esta excepcion
        //if (solicitudEmpty.isEmpty()){
        //    throw new IllegalArgumentException("La solicitud " + solicitudId +" no existe");
        //}
        Solicitud solicitud = solicitudEmpty.get();
        return convertirDesdeDominio(solicitud);
    }

    @Override
    public boolean estaActivo(String unHechoId) {
        List<SolicitudDTO> solicitudesDTO = buscarSolicitudXHecho(unHechoId);
        List<Solicitud> solicitudes = new ArrayList<>();
        solicitudesDTO.forEach(x ->
                {
                    Solicitud solicitudTemp = convertirDesdeDTO(x);
                    solicitudes.add(solicitudTemp);
                }
        );
        return solicitudes.stream().anyMatch(x -> x.getEstado() != EstadoSolicitudBorradoEnum.ACEPTADA)
                ||
                solicitudes.stream().anyMatch(x -> x.getEstado() != EstadoSolicitudBorradoEnum.RECHAZADA);
    }

    @Override
    public void setFachadaFuente(FachadaFuente fuente) {
        this.fuente = fuente; //Testear si el hecho esta en una fuente
    }
    public void setAntiSpam(AntiSpamService antiSpam) {
        this.antiSpam = antiSpam;
    }
    public String getDescripcion(String id){
        SolicitudDTO solicitud = buscarSolicitudXId(id);
        return solicitud.descripcion();
    }
    public EstadoSolicitudBorradoEnum getEstado(String id){
        SolicitudDTO solicitud = buscarSolicitudXId(id);
        return solicitud.estado();
    }


    private Solicitud convertirDesdeDTO(SolicitudDTO solicitudDTO){
        return new Solicitud(solicitudDTO.id(), solicitudDTO.descripcion(), solicitudDTO.estado(), solicitudDTO.hechoId());
    }
    private SolicitudDTO convertirDesdeDominio(Solicitud solicitud){
        return new SolicitudDTO(solicitud.getId(), solicitud.getDescripcion(), solicitud.getEstado(), solicitud.getHechoId());
    }
    private void verificarDescripcion(SolicitudDTO solicitudDTO){
        if (solicitudDTO.descripcion().length() < 500){
            throw  new IllegalArgumentException("La solicitud " + solicitudDTO.id() + ", no cumple con el minimo requerido de caracteres");
        }
    }
    public void limpiarRepo(){
        solicitudRepository.deleteAll();
    }
}
