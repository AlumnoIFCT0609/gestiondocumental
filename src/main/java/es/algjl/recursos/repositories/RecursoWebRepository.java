package es.algjl.recursos.repositories;

import es.algjl.recursos.models.RecursoWeb;
import es.algjl.recursos.models.LicenciaCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoWebRepository extends JpaRepository<RecursoWeb, Long> {
    List<RecursoWeb> findByNombreContainingIgnoreCase(String nombre);
    List<RecursoWeb> findByDescripcionContainingIgnoreCase(String descripcion);
    List<RecursoWeb> findByLicenciaCC(LicenciaCC licencia);
    List<RecursoWeb> findByValidadoCalidad(Boolean validado);
    List<RecursoWeb> findByDireccionWebContaining(String url);
}