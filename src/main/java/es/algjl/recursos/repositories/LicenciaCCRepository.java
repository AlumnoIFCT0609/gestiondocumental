package es.algjl.recursos.repositories;

import es.algjl.recursos.models.LicenciaCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LicenciaCCRepository extends JpaRepository<LicenciaCC, Long> {
    List<LicenciaCC> findByLicenciaContainingIgnoreCase(String licencia);
    Optional<LicenciaCC> findByCodigo(String codigo);
    List<LicenciaCC> findByPropietario(String propietario);
}