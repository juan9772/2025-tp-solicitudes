package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Coleccion {

    public Coleccion(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    private String nombre;
    private String descripcion;
    private LocalDateTime fechaModificacion;

    public void setFechaModificacion(LocalDateTime fechaNueva) {
        fechaModificacion = fechaNueva;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
