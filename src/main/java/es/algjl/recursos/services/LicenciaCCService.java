package es.algjl.recursos.services;

import es.algjl.recursos.models.LicenciaCC;
import es.algjl.recursos.repositories.LicenciaCCRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicenciaCCService {

    private final LicenciaCCRepository licenciaCCRepository;

    @Transactional(readOnly = true)
    public List<LicenciaCC> listarTodas() {
        return licenciaCCRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<LicenciaCC> buscarPorId(Long id) {
        return licenciaCCRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<LicenciaCC> buscarPorCodigo(String codigo) {
        return licenciaCCRepository.findByCodigo(codigo);
    }

    @Transactional
    public LicenciaCC crear(LicenciaCC licencia) {
        log.info("Creando licencia: {}", licencia.getLicencia());
        return licenciaCCRepository.save(licencia);
    }

    @Transactional
    public LicenciaCC actualizar(Long id, LicenciaCC licenciaActualizada) {
        return licenciaCCRepository.findById(id).map(licencia -> {
            licencia.setLicencia(licenciaActualizada.getLicencia());
            licencia.setDescripcion(licenciaActualizada.getDescripcion());
            licencia.setCodigo(licenciaActualizada.getCodigo());
            licencia.setPropietario(licenciaActualizada.getPropietario());
            return licenciaCCRepository.save(licencia);
        }).orElseThrow(() -> new IllegalArgumentException("Licencia no encontrada"));
    }

    @Transactional
    public void eliminar(Long id) {
        licenciaCCRepository.deleteById(id);
    }
}