package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.client.FuenteProxy;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.FuenteDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

record ActualizarSolicitudPayload(EstadoSolicitudBorradoEnum estado, String descripcion) {}

/*
    GET /solicitudes?hechoId={hechoId}
    POST /solicitudes
    GET /solicitudes/{id}
    PATCH /solicitudes/{id}
*/

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class SolicitudController {

    private final FachadaSolicitudes fachadaSolicitudes;
    private final ObjectMapper objectMapper;

    @Autowired
    public SolicitudController(FachadaSolicitudes fachadaSolicitudes, ObjectMapper objectMapper) {
        this.fachadaSolicitudes = fachadaSolicitudes;
        this.objectMapper=objectMapper;
    }

    // GET /solicitudes?hechoId={hechoId}
    @GetMapping("/solicitudes")
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudPorHecho(@RequestParam String hechoId) {
        return ResponseEntity.ok(fachadaSolicitudes.buscarSolicitudXHecho(hechoId));
    }

    // POST /solicitudes
    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudDTO> crearSolicitud(@RequestBody SolicitudDTO solicitud) {
        return ResponseEntity.ok(fachadaSolicitudes.agregar(solicitud));
    }

    // GET /solicitudes/{id}
    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<SolicitudDTO> obtenerSolicitud(@PathVariable String id) {
        return ResponseEntity.ok(fachadaSolicitudes.buscarSolicitudXId(id));
    }

    @PatchMapping("/solicitudes/{solicitudId}")
    public ResponseEntity<SolicitudDTO> actualizarSolicitud(@PathVariable String solicitudId,
                                                            @RequestBody ActualizarSolicitudPayload payload) {
        return ResponseEntity.ok(fachadaSolicitudes.modificar(solicitudId, payload.estado(), payload.descripcion()));
    }
    // GET /solicitudes?hecho={hechoId}
    @GetMapping("/solicitudes/hechos/{id}/estaActivo")
    public ResponseEntity<Boolean> estaActivo(@PathVariable String id) {
        return ResponseEntity.ok(fachadaSolicitudes.estaActivo(id));
    }
    @PostMapping("/solicitudes/fuentes")
    public ResponseEntity<String> setFuente(@RequestBody FuenteDTO fuenteDTO) {
        try {
            // Create a new FuenteProxy instance using the endpoint from the DTO
            var fuenteProxy = new FuenteProxy(this.objectMapper, fuenteDTO.endpoint());

            // Manually inject the proxy into the main facade
            fachadaSolicitudes.setFachadaFuente(fuenteProxy);

            return ResponseEntity.ok("Fuente configurada correctamente con el endpoint: " + fuenteDTO.endpoint());
        } catch (Exception e) {
            // Return a 500 Internal Server Error in case of a configuration issue
            return ResponseEntity.status(500).body("Error al configurar la fuente: " + e.getMessage());
        }
    }
}