package es.algjl.recursos.repositories;

import es.algjl.recursos.models.Pdf;
import es.algjl.recursos.models.LicenciaCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfRepository extends JpaRepository<Pdf, Long> {
    List<Pdf> findByNombreContainingIgnoreCase(String nombre);
    List<Pdf> findByDescripcionContainingIgnoreCase(String descripcion);
    List<Pdf> findByLicenciaCC(LicenciaCC licencia);
    List<Pdf> findByValidadoCalidad(Boolean validado);
}
