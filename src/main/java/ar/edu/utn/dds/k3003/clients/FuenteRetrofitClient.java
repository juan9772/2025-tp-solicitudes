package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FuenteRetrofitClient {
    @GET("/api/hecho/{id}")
    Call<HechoDTO> buscarHechoXId(@Path("id")String id );
}
