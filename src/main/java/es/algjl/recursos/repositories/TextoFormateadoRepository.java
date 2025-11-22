package es.algjl.recursos.repositories;

import es.algjl.recursos.models.TextoFormateado;
import es.algjl.recursos.models.LicenciaCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextoFormateadoRepository extends JpaRepository<TextoFormateado, Long> {
    List<TextoFormateado> findByNombreContainingIgnoreCase(String nombre);
    List<TextoFormateado> findByDescripcionContainingIgnoreCase(String descripcion);
    List<TextoFormateado> findByLicenciaCC(LicenciaCC licencia);
    List<TextoFormateado> findByValidadoCalidad(Boolean validado);
}
