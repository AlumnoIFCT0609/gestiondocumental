package es.algjl.recursos.repositories;

import es.algjl.recursos.models.HojaCalculo;
import es.algjl.recursos.models.LicenciaCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HojaCalculoRepository extends JpaRepository<HojaCalculo, Long> {
    List<HojaCalculo> findByNombreContainingIgnoreCase(String nombre);
    List<HojaCalculo> findByDescripcionContainingIgnoreCase(String descripcion);
    List<HojaCalculo> findByLicenciaCC(LicenciaCC licencia);
    List<HojaCalculo> findByValidadoCalidad(Boolean validado);
}