package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    GET /solicitudes?hecho={hechoId}
    POST /solicitudes
    GET /solicitudes/{id}
    PATCH /solicitudes
*/

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final FachadaSolicitudes fachadaSolicitudes;

    @Autowired
    public SolicitudController(FachadaSolicitudes fachadaSolicitudes) {
        this.fachadaSolicitudes = fachadaSolicitudes;
    }

    // GET /solicitudes?hecho={hechoId}
    @GetMapping
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudPorHecho(@RequestParam String hecho) {
        return ResponseEntity.ok(fachadaSolicitudes.buscarSolicitudXHecho(hecho));
    }

    // POST /solicitudes
    @PostMapping
    public ResponseEntity<SolicitudDTO> crearSolicitud(@RequestBody SolicitudDTO solicitud) {
        return ResponseEntity.ok(fachadaSolicitudes.agregar(solicitud));
    }

    // GET /solicitudes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDTO> obtenerSolicitud(@PathVariable String id) {
        return ResponseEntity.ok(fachadaSolicitudes.buscarSolicitudXId(id));
    }

    @PatchMapping
    public ResponseEntity<SolicitudDTO> actualizarSolicitud(@RequestBody String solicitudId,
                                                            EstadoSolicitudBorradoEnum estado,
                                                            String descripcion) {
        return ResponseEntity.ok(fachadaSolicitudes.modificar(solicitudId, estado, descripcion));
    }
}