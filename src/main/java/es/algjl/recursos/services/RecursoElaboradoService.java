package es.algjl.recursos.services;

import es.algjl.recursos.models.RecursoElaborado;
import es.algjl.recursos.models.Usuario;
import es.algjl.recursos.models.enums.CreadoPara;
import es.algjl.recursos.models.enums.Materia;
import es.algjl.recursos.models.enums.TipoUso;
import es.algjl.recursos.repositories.RecursoElaboradoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecursoElaboradoService {

    private final RecursoElaboradoRepository recursoElaboradoRepository;

    @Transactional(readOnly = true)
    public List<RecursoElaborado> listarTodos() {
        return recursoElaboradoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<RecursoElaborado> listarTodosPaginado(Pageable pageable) {
        return recursoElaboradoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<RecursoElaborado> buscarPorId(Long id) {
        return recursoElaboradoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<RecursoElaborado> buscarPorCreador(Usuario usuario) {
        return recursoElaboradoRepository.findByUsuarioCreador(usuario);
    }

    @Transactional(readOnly = true)
    public List<RecursoElaborado> buscarPorUso(TipoUso uso) {
        return recursoElaboradoRepository.findByUso(uso);
    }

    @Transactional(readOnly = true)
    public List<RecursoElaborado> buscarPorCreadoPara(CreadoPara creadoPara) {
        return recursoElaboradoRepository.findByCreadoPara(creadoPara);
    }

    @Transactional(readOnly = true)
    public List<RecursoElaborado> buscarPorMateria(Materia materia) {
        return recursoElaboradoRepository.findByMateria(materia);
    }

    @Transactional(readOnly = true)
    public List<RecursoElaborado> buscarPorNombre(String nombre) {
        return recursoElaboradoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<RecursoElaborado> buscarValidados() {
        return recursoElaboradoRepository.findByValidadoCalidad(true);
    }

    @Transactional
    public RecursoElaborado crear(RecursoElaborado recurso) {
        log.info("Creando recurso elaborado: {}", recurso.getNombre());
        return recursoElaboradoRepository.save(recurso);
    }

    @Transactional
    public RecursoElaborado actualizar(Long id, RecursoElaborado recursoActualizado) {
        return recursoElaboradoRepository.findById(id).map(recurso -> {
            recurso.setNombre(recursoActualizado.getNombre());
            recurso.setCreadoPara(recursoActualizado.getCreadoPara());
            recurso.setUso(recursoActualizado.getUso());
            recurso.setMateria(recursoActualizado.getMateria());
            recurso.setLicencia(recursoActualizado.getLicencia());
            recurso.setRecursosUsados(recursoActualizado.getRecursosUsados());
            recurso.setTipoTemplate(recursoActualizado.getTipoTemplate());
            recurso.setDatosComposicion(recursoActualizado.getDatosComposicion());
            log.info("Actualizando recurso elaborado: {}", recurso.getNombre());
            return recursoElaboradoRepository.save(recurso);
        }).orElseThrow(() -> new IllegalArgumentException("Recurso elaborado no encontrado"));
    }

    @Transactional
    public void marcarComoValidado(Long id, Boolean validado) {
        recursoElaboradoRepository.findById(id).ifPresent(recurso -> {
            recurso.setValidadoCalidad(validado);
            recursoElaboradoRepository.save(recurso);
            log.info("Recurso elaborado {} marcado como validado: {}", id, validado);
        });
    }

    @Transactional
    public void guardarRutaPdf(Long id, String rutaPdf) {
        recursoElaboradoRepository.findById(id).ifPresent(recurso -> {
            recurso.setRutaPdfGenerado(rutaPdf);
            recursoElaboradoRepository.save(recurso);
            log.info("Ruta PDF guardada para recurso elaborado: {}", id);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        if (recursoElaboradoRepository.existsById(id)) {
            recursoElaboradoRepository.deleteById(id);
            log.info("Recurso elaborado eliminado con ID: {}", id);
        }
    }
}