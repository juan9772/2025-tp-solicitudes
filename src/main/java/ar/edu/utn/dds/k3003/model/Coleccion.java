package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // Changed this import
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Coleccion {

    public Coleccion(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    @Id // This annotation now comes from jakarta.persistence
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
