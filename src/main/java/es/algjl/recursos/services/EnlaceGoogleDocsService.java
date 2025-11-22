package es.algjl.recursos.services;

import es.algjl.recursos.models.EnlaceGoogleDocs;
import es.algjl.recursos.repositories.EnlaceGoogleDocsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnlaceGoogleDocsService {

    private final EnlaceGoogleDocsRepository enlaceGoogleDocsRepository;

    @Transactional(readOnly = true)
    public List<EnlaceGoogleDocs> listarTodos() {
        return enlaceGoogleDocsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<EnlaceGoogleDocs> buscarPorId(Long id) {
        return enlaceGoogleDocsRepository.findById(id);
    }

    @Transactional
    public EnlaceGoogleDocs crear(EnlaceGoogleDocs enlace) {
        log.info("Creando enlace Google Docs: {}", enlace.getDireccionWeb());
        return enlaceGoogleDocsRepository.save(enlace);
    }

    @Transactional
    public EnlaceGoogleDocs actualizar(Long id, EnlaceGoogleDocs enlaceActualizado) {
        return enlaceGoogleDocsRepository.findById(id).map(enlace -> {
            enlace.setNombre(enlaceActualizado.getNombre());
            enlace.setDescripcion(enlaceActualizado.getDescripcion());
            enlace.setDireccionWeb(enlaceActualizado.getDireccionWeb());
            enlace.setCuentaAcceso(enlaceActualizado.getCuentaAcceso());
            enlace.setPasswordAcceso(enlaceActualizado.getPasswordAcceso());
            enlace.setLicenciaCC(enlaceActualizado.getLicenciaCC());
            return enlaceGoogleDocsRepository.save(enlace);
        }).orElseThrow(() -> new IllegalArgumentException("Enlace Google Docs no encontrado"));
    }

    @Transactional
    public void marcarComoValidado(Long id, Boolean validado) {
        enlaceGoogleDocsRepository.findById(id).ifPresent(enlace -> {
            enlace.setValidadoCalidad(validado);
            enlaceGoogleDocsRepository.save(enlace);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        enlaceGoogleDocsRepository.deleteById(id);
    }
}