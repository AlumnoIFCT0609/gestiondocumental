package es.algjl.recursos.repositories;

import es.algjl.recursos.models.TextoPlano;
import es.algjl.recursos.models.LicenciaCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextoPlanoRepository extends JpaRepository<TextoPlano, Long> {
    List<TextoPlano> findByNombreContainingIgnoreCase(String nombre);
    List<TextoPlano> findByDescripcionContainingIgnoreCase(String descripcion);
    List<TextoPlano> findByLicenciaCC(LicenciaCC licencia);
    List<TextoPlano> findByValidadoCalidad(Boolean validado);
}