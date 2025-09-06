package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.AntiSpamService;
import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.repository.JpaSolicitudRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JpaSolicitudRepository repo;

    @MockBean
    private Fachada instancia;
    @MockBean
    private FachadaFuente fachadaFuente;
    @MockBean
    private AntiSpamService antiSpam;

    @SneakyThrows
    @BeforeEach
    void setUp(){
        when(instancia.buscarSolicitudXId("1")).thenReturn(
                new SolicitudDTO("1", "Solicitud1", EstadoSolicitudBorradoEnum.CREADA, "2"));
        when(fachadaFuente.buscarHechoXId("2")).thenReturn(
                new HechoDTO("2", "TituloColeccion","TituloHecho"));
        when(antiSpam.revisarSpam("Solicitud1")).thenReturn(false);
    }

    @Test
    @DisplayName("Get de una solicitud inexistente")
    public void getSolicitudNoExistente() throws Exception {
        when(instancia.buscarSolicitudXId("2"))
                .thenThrow(new NoSuchElementException("No value present"));
        mockMvc.perform(get("/api/solicitudes/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No value present"));
    }

    @Test
    @DisplayName("Get de una solicitud existente por ID")
    public void getSolicitudExistenteSolicitudID() throws Exception {
        mockMvc.perform(get("/api/solicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"1\",\"descripcion\":\"Solicitud1\",\"estado\":\"CREADA\",\"hecho_id\":\"2\"}"));
    }

    @Test
    @DisplayName("Get de una solicitud existente por ID de hecho")
    public void getSolicitudExistenteHechoID() throws Exception {
        ArrayList<SolicitudDTO> respuesta = new ArrayList<>();
        respuesta.add(new SolicitudDTO("1", "Solicitud1", EstadoSolicitudBorradoEnum.CREADA, "2"));

        when(instancia.buscarSolicitudXHecho("2")).thenReturn(respuesta);

        mockMvc.perform(get("/api/solicitudes?hecho=2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":\"1\",\"descripcion\":\"Solicitud1\",\"estado\":\"CREADA\",\"hecho_id\":\"2\"}]"));
    }

    @Test
    @DisplayName("Post de una solicitud")
    public void postSolicitud() throws Exception {
        SolicitudDTO solicitud = new SolicitudDTO("1", "Solicitud1", EstadoSolicitudBorradoEnum.CREADA, "2");
        when(instancia.agregar(solicitud)).thenReturn(solicitud);

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.descripcion").value("Solicitud1"))
                .andExpect(jsonPath("$.estado").value("CREADA"))
                .andExpect(jsonPath("$.hecho_id").value("2"));
    }

    @Test
    @DisplayName("Patch de una solicitud")
    public void patchSolicitud() throws Exception {
        SolicitudDTO solicitud = new SolicitudDTO("1", "NuevaDesc", EstadoSolicitudBorradoEnum.RECHAZADA, "2");
        when(instancia.modificar("1", EstadoSolicitudBorradoEnum.RECHAZADA, "RECHAZADA")).thenReturn(solicitud);
        String json = """
            {
              "id": "1",
              "descripcion": "NuevaDesc",
              "estado": "RECHAZADA"
            }
            """;

        mockMvc.perform(patch("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}