package es.algjl.recursos.repositories;

import es.algjl.recursos.models.Imagen;
import es.algjl.recursos.models.LicenciaCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByNombreContainingIgnoreCase(String nombre);
    List<Imagen> findByDescripcionContainingIgnoreCase(String descripcion);
    List<Imagen> findByLicenciaCC(LicenciaCC licencia);
    List<Imagen> findByValidadoCalidad(Boolean validado);
}