package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


public class FuenteProxy implements FachadaFuente {

    final private String endpoint;
    private final FuenteRetrofitClient service;

    // Primer constructor: Usa el endpoint del entorno (para inyección por Spring)
    public FuenteProxy(ObjectMapper objectMapper) {
        var env = System.getenv();
        this.endpoint = env.getOrDefault("Fuente", "https://two025-tp-entrega-2-juan9772-1.onrender.com/");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(FuenteRetrofitClient.class);
    }

    // --- NUEVO CONSTRUCTOR ---
    // Segundo constructor: Permite pasar el endpoint dinámicamente.
    public FuenteProxy(ObjectMapper objectMapper, String endpoint) {
        this.endpoint = endpoint; // Usa el endpoint que viene como parámetro

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(FuenteRetrofitClient.class);
    }



    @Override
    public ColeccionDTO agregar(ColeccionDTO coleccionDTO) {
        return null;
    }

    @Override
    public ColeccionDTO buscarColeccionXId(String coleccionId) throws NoSuchElementException {
        return null;
    }

    @Override
    public HechoDTO agregar(HechoDTO hechoDTO) {
        return null;
    }

    @Override
    @SneakyThrows
    public HechoDTO buscarHechoXId(String id) throws NoSuchElementException {
        try {
            Response<HechoDTO> response = service.buscarHechoXId(id).execute();

            if (response.isSuccessful()) {
                return response.body();
            }
            if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
                throw new NoSuchElementException("No se encontró el hecho con ID: " + id);
            }
            throw new RuntimeException("Error conectándose con el componente de fuentes.");
        } catch (IOException e) {
            throw new RuntimeException("Error de I/O al conectarse con el componente de fuentes.", e);
        }
    }

    @Override
    public List<HechoDTO> buscarHechosXColeccion(String coleccionId) throws NoSuchElementException {
        return List.of();
    }

    @Override
    public void setProcesadorPdI(FachadaProcesadorPdI procesador) {

    }

    @Override
    public PdIDTO agregar(PdIDTO pdIDTO) throws IllegalStateException {
        return null;
    }

    @Override
    public List<ColeccionDTO> colecciones() {
        return List.of();
    }
}
