package ar.edu.utn.dds.k3003.client;

import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FuenteRetrofitClient {
  @GET("api/hechos/{id}")
  Call<HechoDTO> buscarHechoXId(@Path("id") String id);

  @GET("api/colecciones")
  Call<List<ColeccionDTO>> getCollecciones();

  @GET("api/colecciones/{coleccionId}/hechos")
  Call<List<HechoDTO>> getHechosPorColleccion(@Path("coleccionId") String coleccionId);
}
