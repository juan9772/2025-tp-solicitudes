package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.Solicitud;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
@Profile("!test")
public interface JpaSolicitudRepository extends JpaRepository<Solicitud, String>, SolicitudRepository {
    @Override
    @Modifying
    @Query("DELETE FROM Solicitud s WHERE s.id = :id")
    @Transactional
    void delete(@Param("id") String id);

    @Override
    @Query("SELECT s FROM Solicitud s WHERE s.id = :id")
    @NotNull
    Optional<Solicitud> findById(@NotNull String id);

    //@Modifying(clearAutomatically = true)
    //@Transactional
    //@Query(value = "INSERT INTO Solicitud (id, descripcion, estado, hechoId) values (:#{#s.id},:#{#s.descripcion},  :#{#s.estado} ,:#{#s.hechoId})",
    //        nativeQuery = true)
    //Solicitud save(@Param("s") Solicitud solicitud);
}