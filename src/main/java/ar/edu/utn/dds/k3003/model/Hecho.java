package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id; // Changed this import

@Data
@Entity
public class Hecho {
//
    public Hecho(String id,String nombreColeccion, String titulo, List<String> etiquetas, CategoriaHechoEnum categoria, String ubicacion, LocalDateTime fecha, String origen) {
        this.id = id;
        this.nombreColeccion = nombreColeccion;
        this.titulo = titulo;
        this.etiquetas = etiquetas;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.origen = origen;
    }

    @Id
    private String id;

    //aca fk
    private String nombreColeccion;

    @ElementCollection
    @CollectionTable(name = "hecho_etiquetas", joinColumns = @JoinColumn(name = "hecho_id"))
    @Column(name = "etiqueta")
    private List<String> etiquetas;
    private String titulo;

    @Enumerated(EnumType.STRING)
    private CategoriaHechoEnum categoria;

    private String ubicacion;
    private LocalDateTime fecha;
    private String origen;

    public String getTitulo() {
        return titulo;
    }
}
