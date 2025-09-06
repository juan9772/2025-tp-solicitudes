package ar.edu.utn.dds.k3003.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;

import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.repository.JpaSolicitudRepository;
import ar.edu.utn.dds.k3003.tests.TestTP;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SolicitudTest implements TestTP<FachadaSolicitudes> {

    private static final String HECHO_ID = "123";
    private static final String TITULO_COLECCION = "123";

    Fachada instancia;
    @Mock
    FachadaFuente fachadaFuente;
    @Mock
    AntiSpamService antiSpam;
    @Autowired
    JpaSolicitudRepository repo;

    @SneakyThrows
    @BeforeEach
    void setUp(){
        instancia = new Fachada(repo);
        instancia.setFachadaFuente(fachadaFuente);
        instancia.setAntiSpam(antiSpam);
    }

    @Test
    @DisplayName("Agregar una solicitud")
    void testAgregarSolicitud() {
        String tituloHecho = "UnTitulo Hecho";
        when(fachadaFuente.buscarHechoXId(HECHO_ID)).thenReturn(
                new HechoDTO(HECHO_ID, TITULO_COLECCION,tituloHecho));
        when(antiSpam.revisarSpam("una solicitud")).thenReturn(false);
        var solicitudDTO = instancia.agregar(
                new SolicitudDTO("la", "una solicitud", EstadoSolicitudBorradoEnum.CREADA, HECHO_ID));

        assertNotNull(solicitudDTO.id(), "La solicitud tendria que tener un identificador");

        verify(fachadaFuente, description("la")).buscarHechoXId(HECHO_ID);
    }

    @Test
    @DisplayName("Intentar agregar una solicitud a un hecho que no existe")
    void testAgregarSolicitudNoExisteHecho() {
        when(fachadaFuente.buscarHechoXId(HECHO_ID)).thenThrow(NoSuchElementException.class);
        assertThrows(
                NoSuchElementException.class,
                () -> instancia.agregar(
                        new SolicitudDTO("", "una solicitud", EstadoSolicitudBorradoEnum.CREADA, HECHO_ID)),
                "Si el hecho no existe, el agregado de la solicitud deberia fallar");
    }

    @Test
    @DisplayName("Intentar agregar solicitud Spam")
    void testAgregarSolicitudSpam() {
        String tituloHecho = "UnTitulo Hecho";
        when(fachadaFuente.buscarHechoXId(HECHO_ID)).thenReturn(
                new HechoDTO(HECHO_ID, TITULO_COLECCION,tituloHecho));
        when(antiSpam.revisarSpam("una solicitud")).thenReturn(true);
        assertThrows(
                IllegalArgumentException.class,
                () -> instancia.agregar(
                        new SolicitudDTO("", "una solicitud", EstadoSolicitudBorradoEnum.CREADA, HECHO_ID)),
                "Si el hecho no existe, el agregado de la solicitud deberia fallar");
    }

    @Test
    @DisplayName("Intentar buscar una solicitud por ID")
    void testBuscarSolicitudId() {
        String tituloHecho = "UnTitulo Hecho";
        when(fachadaFuente.buscarHechoXId(HECHO_ID)).thenReturn(
                new HechoDTO(HECHO_ID, TITULO_COLECCION,tituloHecho));
        when(antiSpam.revisarSpam("una solicitud")).thenReturn(false);

        var solicitudDTO = instancia.agregar(
                new SolicitudDTO("la2", "una solicitud", EstadoSolicitudBorradoEnum.CREADA, HECHO_ID));
        assertEquals(solicitudDTO.id(), instancia.buscarSolicitudXId("la2").id());
    }

    @Test
    @DisplayName("Intentar buscar una solicitud por hecho")
    void testBuscarSolicitudHecho() {
        String tituloHecho = "UnTitulo Hecho";
        when(fachadaFuente.buscarHechoXId(HECHO_ID)).thenReturn(
                new HechoDTO(HECHO_ID, TITULO_COLECCION,tituloHecho));
        when(fachadaFuente.buscarHechoXId(HECHO_ID)).thenReturn(
                new HechoDTO("otroID", TITULO_COLECCION,tituloHecho));
        when(antiSpam.revisarSpam("una solicitud")).thenReturn(false);

        var solicitudDTO = instancia.agregar(
                new SolicitudDTO("la5", "una solicitud", EstadoSolicitudBorradoEnum.CREADA, HECHO_ID));
        var mismoHechoSolicitudDTO = instancia.agregar(
                new SolicitudDTO("la6", "una solicitud", EstadoSolicitudBorradoEnum.CREADA, HECHO_ID));
        var otraSolicitudDTO = instancia.agregar(
                new SolicitudDTO("la7", "una solicitud", EstadoSolicitudBorradoEnum.CREADA, "otroID"));
        assertEquals(7, instancia.buscarSolicitudXHecho(HECHO_ID).size());
    }

    @Test
    @DisplayName("Intentar cambiar el estado y descripcion de una solicitud")
    void testCambiarSolicitudEstadoDescripcion() {
        String tituloHecho = "UnTitulo Hecho";
        when(fachadaFuente.buscarHechoXId(HECHO_ID)).thenReturn(
                new HechoDTO(HECHO_ID, TITULO_COLECCION,tituloHecho));
        when(antiSpam.revisarSpam("una solicitud")).thenReturn(false);

        var solicitudDTO = instancia.agregar(
                new SolicitudDTO("la10", "una solicitud", EstadoSolicitudBorradoEnum.CREADA, HECHO_ID));
        instancia.modificar("la10", EstadoSolicitudBorradoEnum.VALIDADA, "Nueva Descripcion");
        assertEquals("Nueva Descripcion", instancia.buscarSolicitudXId("la10").descripcion());
        assertEquals(EstadoSolicitudBorradoEnum.VALIDADA, instancia.buscarSolicitudXId("la10").estado());
    }

    @Test
    @DisplayName("Verificar si una solicitud sigue activo")
    void testSolicitudActiva() {
        String tituloHecho = "UnTitulo Hecho";
        when(fachadaFuente.buscarHechoXId(HECHO_ID)).thenReturn(
                new HechoDTO(HECHO_ID, TITULO_COLECCION,tituloHecho));
        when(antiSpam.revisarSpam("una solicitud")).thenReturn(false);

        var solicitudDTO = instancia.agregar(
                new SolicitudDTO("la101", "una solicitud", EstadoSolicitudBorradoEnum.EN_DISCUCION, HECHO_ID));
        var otraSolicitudDTO = instancia.agregar(
                new SolicitudDTO("la102", "otra solicitud", EstadoSolicitudBorradoEnum.ACEPTADA, HECHO_ID));
        var rechazadaSolicitudDTO = instancia.agregar(
                new SolicitudDTO("la115", "otra solicitud", EstadoSolicitudBorradoEnum.RECHAZADA, HECHO_ID));
        assertTrue(instancia.estaActivo(HECHO_ID));
    }

    @Override
    public String paquete() {
        return PAQUETE_BASE + "tests.solicitudes";
    }

    @Override
    public Class<FachadaSolicitudes> clase() {
        return FachadaSolicitudes.class;
    }
}
