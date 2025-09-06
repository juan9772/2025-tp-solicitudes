package ar.edu.utn.dds.k3003.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.model.Solicitud;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SolicitudRepoTest {

    private static final String HECHO_ID = "123";
    private static final String SOLICITUD_ID = "9";
    private static final String DESCRIPCION = "Una descripcion";
    Solicitud solicitud = new Solicitud(SOLICITUD_ID, DESCRIPCION, EstadoSolicitudBorradoEnum.CREADA, HECHO_ID);

    @Autowired
    JpaSolicitudRepository instancia;

    @Test
    @DisplayName("Agregar una solicitud y buscarla")
    void testAgregarSolicitud() {
        instancia.save(solicitud);
        assertEquals(solicitud.getId(), instancia.findById(SOLICITUD_ID).get().getId());
    }

    @Test
    @DisplayName("Borrar una solicitud")
    void testBorrarSolicitud() {
        instancia.save(solicitud);
        instancia.delete(SOLICITUD_ID);
        assertEquals(Optional.empty(), instancia.findById(SOLICITUD_ID));
    }

    @Test
    @Disabled
    @DisplayName("Borrar una solicitud que no existe")
    void testBorrarSolicitudNoExistente() {
        instancia.save(solicitud);
        instancia.delete(SOLICITUD_ID);
        assertThrows(
                IllegalArgumentException.class,
                () -> instancia.delete(SOLICITUD_ID),
                "No value present");
    }

    @Test
    @DisplayName("Buscar una solicitud por el ID del Hecho")
    void testBuscarSolicitudPorHecho() {
        Solicitud otraSolicitud = new Solicitud("16621", "Desc", EstadoSolicitudBorradoEnum.CREADA, "515");
        Solicitud otraSolicitudDiferente = new Solicitud("16621", "Desc", EstadoSolicitudBorradoEnum.CREADA, "1551");
        instancia.save(otraSolicitud);
        instancia.save(solicitud);
        instancia.save(otraSolicitudDiferente);
        assertEquals(1, instancia.findByHechoId(HECHO_ID).size());
    }
}
