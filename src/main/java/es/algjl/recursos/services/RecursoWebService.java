package es.algjl.recursos.services;

import es.algjl.recursos.models.RecursoWeb;
import es.algjl.recursos.repositories.RecursoWebRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecursoWebService {

    private final RecursoWebRepository recursoWebRepository;

    @Transactional(readOnly = true)
    public List<RecursoWeb> listarTodos() {
        return recursoWebRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<RecursoWeb> buscarPorId(Long id) {
        return recursoWebRepository.findById(id);
    }

    @Transactional
    public RecursoWeb crear(RecursoWeb recurso) {
        log.info("Creando recurso web: {}", recurso.getDireccionWeb());
        return recursoWebRepository.save(recurso);
    }

    @Transactional
    public RecursoWeb actualizar(Long id, RecursoWeb recursoActualizado) {
        return recursoWebRepository.findById(id).map(recurso -> {
            recurso.setNombre(recursoActualizado.getNombre());
            recurso.setDescripcion(recursoActualizado.getDescripcion());
            recurso.setDireccionWeb(recursoActualizado.getDireccionWeb());
            recurso.setLicenciaCC(recursoActualizado.getLicenciaCC());
            return recursoWebRepository.save(recurso);
        }).orElseThrow(() -> new IllegalArgumentException("Recurso web no encontrado"));
    }

    @Transactional
    public void marcarComoValidado(Long id, Boolean validado) {
        recursoWebRepository.findById(id).ifPresent(recurso -> {
            recurso.setValidadoCalidad(validado);
            recursoWebRepository.save(recurso);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        recursoWebRepository.deleteById(id);
    }
}