package ar.edu.utn.dds.k3003.client;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.http.HttpStatus;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class FuenteProxy implements FachadaFuente {
  final private String endpoint;
  private final FuenteRetrofitClient service;
  private static final Logger logger = LoggerFactory.getLogger(FuenteProxy.class);

  // Primer constructor: Usa el endpoint del entorno (para inyección por Spring)
  public FuenteProxy(ObjectMapper objectMapper) {
    var env = System.getenv();
    String base = env.getOrDefault("Fuente", "https://two025-tp-fuente2.onrender.com/");
    // Ensure baseUrl ends with '/'
    if (!base.endsWith("/")) {
      base = base + "/";
    }
    this.endpoint = base;

    logger.info("FuenteProxy inicializado con endpoint='{}' (desde env)", this.endpoint);

    // Allow unknown properties and accept camelCase property names from remote fuentes
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
    // Register mixin so 'nombreColeccion' (camelCase) is accepted for HechoDTO
    objectMapper.addMixIn(ar.edu.utn.dds.k3003.facades.dtos.HechoDTO.class, HechoDTOMixin.class);

    try {
      var retrofit =
          new Retrofit.Builder()
              .baseUrl(this.endpoint)
              .addConverterFactory(JacksonConverterFactory.create(objectMapper))
              .build();

      this.service = retrofit.create(FuenteRetrofitClient.class);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(
          "Endpoint inválido para Fuente: '" + this.endpoint + "' - " + e.getMessage(), e);
    }
  }

  // --- NUEVO CONSTRUCTOR ---
  // Segundo constructor: Permite pasar el endpoint dinámicamente.
  public FuenteProxy(ObjectMapper objectMapper, String endpoint) {
    String base = endpoint; // Usa el endpoint que viene como parámetro
    // Normalize endpoint: Retrofit requires baseUrl to end with '/'
    if (base != null && !base.endsWith("/")) {
      base = base + "/";
    }
    this.endpoint = base;

    logger.info("FuenteProxy inicializado con endpoint='{}' (param) ", this.endpoint);

    // Allow unknown properties and accept camelCase property names from remote fuentes
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
    // Register mixin so 'nombreColeccion' (camelCase) is accepted for HechoDTO
    objectMapper.addMixIn(ar.edu.utn.dds.k3003.facades.dtos.HechoDTO.class, HechoDTOMixin.class);

    try {
      var retrofit =
          new Retrofit.Builder()
              .baseUrl(this.endpoint)
              .addConverterFactory(JacksonConverterFactory.create(objectMapper))
              .build();

      this.service = retrofit.create(FuenteRetrofitClient.class);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(
          "Endpoint inválido para Fuente: '" + this.endpoint + "' - " + e.getMessage(), e);
    }
  }

  @Override
  public List<ColeccionDTO> colecciones() {
    try {
      logger.debug("FuenteProxy: solicitando colecciones a endpoint='{}'", this.endpoint);
      Response<List<ColeccionDTO>> response = service.getCollecciones().execute();
      logger.debug(
          "FuenteProxy: colecciones response code={} message='{}'",
          response.code(),
          response.message());
      if (response.isSuccessful() && response.body() != null) {
        logger.debug("FuenteProxy: colecciones body size={}", response.body().size());
        return response.body();
      }
      logger.error(
          "FuenteProxy: error al obtener colecciones desde endpoint='{}' mensaje='{}'",
          this.endpoint,
          response.message());
      throw new RuntimeException("Error al obtener colecciones: " + response.message());
    } catch (IOException e) {
      logger.error(
          "FuenteProxy: IOException solicitando colecciones a endpoint='{}'", this.endpoint, e);
      throw new RuntimeException("Error de I/O al conectarse con el componente de fuentes.", e);
    }
  }

  @Override
  public List<HechoDTO> buscarHechosXColeccion(String coleccionId) throws NoSuchElementException {
    try {
      logger.debug(
          "FuenteProxy: solicitando hechos a endpoint='{}' para coleccion='{}'",
          this.endpoint,
          coleccionId);
      Response<List<HechoDTO>> response = service.getHechosPorColleccion(coleccionId).execute();
      logger.debug(
          "FuenteProxy: response code={} mensaje='{}'", response.code(), response.message());
      if (response.isSuccessful() && response.body() != null) {
        logger.debug(
            "FuenteProxy: body size={} para coleccion='{}'", response.body().size(), coleccionId);
        return response.body();
      }
      if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
        logger.info(
            "FuenteProxy: 404 Not Found para coleccion='{}' endpoint='{}'",
            coleccionId,
            this.endpoint);
        throw new NoSuchElementException("No se encontraron hechos para la colección: " + coleccionId);
      }
      logger.error(
          "FuenteProxy: error al buscar hechos para coleccion='{}' endpoint='{}' mensaje='{}'",
          coleccionId,
          this.endpoint,
          response.message());
      throw new RuntimeException("Error al buscar hechos para la colección: " + response.message());
    } catch (IOException e) {
      logger.error(
          "FuenteProxy: IOException solicitando hechos a endpoint='{}' para coleccion='{}'",
          this.endpoint,
          coleccionId,
          e);
      throw new RuntimeException("Error de I/O al conectarse con el componente de fuentes.", e);
    }
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
  public HechoDTO buscarHechoXId(String hechoId) throws NoSuchElementException {
    if (hechoId == null || hechoId.trim().isEmpty()) {
      throw new IllegalArgumentException("El ID del hecho no puede ser nulo o vacío.");
    }
    try {
      logger.debug(
          "FuenteProxy: solicitando hecho a endpoint='{}' para hechoId='{}'", this.endpoint, hechoId);
      Response<HechoDTO> response = service.buscarHechoXId(hechoId).execute();
      logger.debug(
          "FuenteProxy: response code={} mensaje='{}'", response.code(), response.message());

      if (response.isSuccessful() && response.body() != null) {
        logger.debug("FuenteProxy: body para hechoId='{}' -> {}", hechoId, response.body());
        return response.body();
      }
      if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
        logger.info(
            "FuenteProxy: 404 Not Found para hechoId='{}' endpoint='{}'", hechoId, this.endpoint);
        throw new NoSuchElementException("No se encontró el hecho con ID: " + hechoId);
      }
      logger.error(
          "FuenteProxy: error al buscar hecho para hechoId='{}' endpoint='{}' mensaje='{}'",
          hechoId,
          this.endpoint,
          response.message());
      throw new RuntimeException("Error al buscar el hecho: " + response.message());
    } catch (IOException e) {
      logger.error(
          "FuenteProxy: IOException solicitando hecho a endpoint='{}' para hechoId='{}'",
          this.endpoint,
          hechoId,
          e);
      throw new RuntimeException("Error de I/O al conectarse con el componente de fuentes.", e);
    }
  }

  public void modificarHecho(String id, Map<String, String> payload) {
    try {
      logger.debug(
          "FuenteProxy: modificando hecho con id={} en endpoint='{}'", id, this.endpoint);
      Response<Void> response = service.modificar(id, payload).execute();
      logger.debug(
          "FuenteProxy: response code={} mensaje='{}'", response.code(), response.message());

      if (!response.isSuccessful()) {
        logger.error(
            "FuenteProxy: error al modificar hecho con id={} en endpoint='{}' mensaje='{}'",
            id,
            this.endpoint,
            response.message());
        throw new RuntimeException("Error al modificar el hecho: " + response.message());
      }
       logger.info("FuenteProxy: Hecho con id={} modificado exitosamente.", id);

    } catch (IOException e) {
      logger.error(
          "FuenteProxy: IOException modificando hecho en endpoint='{}' para id='{}'",
          this.endpoint,
          id,
          e);
      throw new RuntimeException("Error de I/O al conectarse con el componente de fuentes.", e);
    }
  }

  @Override
  public void setProcesadorPdI(FachadaProcesadorPdI procesador) {}

  @Override
  public PdIDTO agregar(PdIDTO pdIDTO) throws IllegalStateException {
    return null;
  }
}
