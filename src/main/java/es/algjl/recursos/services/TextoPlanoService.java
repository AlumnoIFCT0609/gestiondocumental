package es.algjl.recursos.services;

import es.algjl.recursos.models.TextoPlano;
import es.algjl.recursos.repositories.TextoPlanoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextoPlanoService {

    private final TextoPlanoRepository textoPlanoRepository;

    @Transactional(readOnly = true)
    public List<TextoPlano> listarTodos() {
        return textoPlanoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TextoPlano> buscarPorId(Long id) {
        return textoPlanoRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public List<TextoPlano> buscarPorNombre(String nombre) {
        return textoPlanoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
   
    @Transactional
    public TextoPlano crear(TextoPlano texto) {
        log.info("Creando texto plano: {}", texto.getNombre());
        return textoPlanoRepository.save(texto);
    }

    @Transactional
    public TextoPlano actualizar(Long id, TextoPlano textoActualizado) {
        return textoPlanoRepository.findById(id).map(texto -> {
            texto.setNombre(textoActualizado.getNombre());
            texto.setDescripcion(textoActualizado.getDescripcion());
            texto.setLicenciaCC(textoActualizado.getLicenciaCC());
            return textoPlanoRepository.save(texto);
        }).orElseThrow(() -> new IllegalArgumentException("Texto plano no encontrado"));
    }

    @Transactional
    public void marcarComoValidado(Long id, Boolean validado) {
        textoPlanoRepository.findById(id).ifPresent(texto -> {
            texto.setValidadoCalidad(validado);
            textoPlanoRepository.save(texto);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        textoPlanoRepository.deleteById(id);
    }
}