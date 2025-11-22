package es.algjl.recursos.services;

import es.algjl.recursos.models.Imagen;
import es.algjl.recursos.repositories.ImagenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImagenService {

    private final ImagenRepository imagenRepository;

    @Transactional(readOnly = true)
    public List<Imagen> listarTodas() {
        return imagenRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Imagen> buscarPorId(Long id) {
        return imagenRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Imagen> buscarPorNombre(String nombre) {
        return imagenRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Imagen> buscarValidadas() {
        return imagenRepository.findByValidadoCalidad(true);
    }

    @Transactional
    public Imagen crear(Imagen imagen) {
        log.info("Creando imagen: {}", imagen.getNombre());
        return imagenRepository.save(imagen);
    }

    @Transactional
    public Imagen actualizar(Long id, Imagen imagenActualizada) {
        return imagenRepository.findById(id).map(imagen -> {
            imagen.setNombre(imagenActualizada.getNombre());
            imagen.setDescripcion(imagenActualizada.getDescripcion());
            imagen.setLicenciaCC(imagenActualizada.getLicenciaCC());
            imagen.setDirectorioOrigen(imagenActualizada.getDirectorioOrigen());
            return imagenRepository.save(imagen);
        }).orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada"));
    }

    @Transactional
    public void marcarComoValidada(Long id, Boolean validada) {
        imagenRepository.findById(id).ifPresent(imagen -> {
            imagen.setValidadoCalidad(validada);
            imagenRepository.save(imagen);
        });
    }

    @Transactional
    public void incrementarUsos(Long id) {
        imagenRepository.findById(id).ifPresent(imagen -> {
            imagen.incrementarUsos();
            imagenRepository.save(imagen);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        if (imagenRepository.existsById(id)) {
            imagenRepository.deleteById(id);
            log.info("Imagen eliminada con ID: {}", id);
        }
    }
}