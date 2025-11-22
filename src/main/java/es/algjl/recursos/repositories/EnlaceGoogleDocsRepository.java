package es.algjl.recursos.repositories;

import es.algjl.recursos.models.EnlaceGoogleDocs;
import es.algjl.recursos.models.LicenciaCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnlaceGoogleDocsRepository extends JpaRepository<EnlaceGoogleDocs, Long> {
    List<EnlaceGoogleDocs> findByNombreContainingIgnoreCase(String nombre);
    List<EnlaceGoogleDocs> findByDescripcionContainingIgnoreCase(String descripcion);
    List<EnlaceGoogleDocs> findByLicenciaCC(LicenciaCC licencia);
    List<EnlaceGoogleDocs> findByValidadoCalidad(Boolean validado);
    List<EnlaceGoogleDocs> findByDireccionWebContaining(String url);
}