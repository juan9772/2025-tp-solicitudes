package ar.edu.utn.dds.k3003.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class HechoDTOMixin {
    @JsonProperty("nombreColeccion")
    private String coleccionId;
}
