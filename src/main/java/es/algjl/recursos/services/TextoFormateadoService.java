package es.algjl.recursos.services;

import es.algjl.recursos.models.TextoFormateado;
import es.algjl.recursos.repositories.TextoFormateadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextoFormateadoService {

    private final TextoFormateadoRepository textoFormateadoRepository;

    @Transactional(readOnly = true)
    public List<TextoFormateado> listarTodos() {
        return textoFormateadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TextoFormateado> buscarPorId(Long id) {
        return textoFormateadoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<TextoFormateado> buscarPorNombre(String nombre) {
        return textoFormateadoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public TextoFormateado crear(TextoFormateado texto) {
        log.info("Creando texto formateado: {}", texto.getNombre());
        return textoFormateadoRepository.save(texto);
    }

    @Transactional
    public TextoFormateado actualizar(Long id, TextoFormateado textoActualizado) {
        return textoFormateadoRepository.findById(id).map(texto -> {
            texto.setNombre(textoActualizado.getNombre());
            texto.setDescripcion(textoActualizado.getDescripcion());
            texto.setLicenciaCC(textoActualizado.getLicenciaCC());
            texto.setDirectorioOrigen(textoActualizado.getDirectorioOrigen());
            return textoFormateadoRepository.save(texto);
        }).orElseThrow(() -> new IllegalArgumentException("Texto formateado no encontrado"));
    }

    @Transactional
    public void marcarComoValidado(Long id, Boolean validado) {
        textoFormateadoRepository.findById(id).ifPresent(texto -> {
            texto.setValidadoCalidad(validado);
            textoFormateadoRepository.save(texto);
        });
    }

    @Transactional
    public void incrementarUsos(Long id) {
        textoFormateadoRepository.findById(id).ifPresent(texto -> {
            texto.incrementarUsos();
            textoFormateadoRepository.save(texto);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        textoFormateadoRepository.deleteById(id);
    }
}