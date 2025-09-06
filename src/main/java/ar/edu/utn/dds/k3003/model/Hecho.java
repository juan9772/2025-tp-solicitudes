package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
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

    private String id;
    private String nombreColeccion;
    private List<String> etiquetas;
    private String titulo;
    private CategoriaHechoEnum categoria;
    private String ubicacion;
    private LocalDateTime fecha;
    private String origen;

    public String getTitulo() {
        return titulo;
    }
}
