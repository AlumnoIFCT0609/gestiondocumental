package es.algjl.recursos.services;

import es.algjl.recursos.models.Pdf;
import es.algjl.recursos.repositories.PdfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    private final PdfRepository pdfRepository;

    @Transactional(readOnly = true)
    public List<Pdf> listarTodos() {
        return pdfRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Pdf> buscarPorId(Long id) {
        return pdfRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Pdf> buscarPorNombre(String nombre) {
        return pdfRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public Pdf crear(Pdf pdf) {
        log.info("Creando PDF: {}", pdf.getNombre());
        return pdfRepository.save(pdf);
    }

    @Transactional
    public Pdf actualizar(Long id, Pdf pdfActualizado) {
        return pdfRepository.findById(id).map(pdf -> {
            pdf.setNombre(pdfActualizado.getNombre());
            pdf.setDescripcion(pdfActualizado.getDescripcion());
            pdf.setLicenciaCC(pdfActualizado.getLicenciaCC());
            pdf.setDirectorioOrigen(pdfActualizado.getDirectorioOrigen());
            return pdfRepository.save(pdf);
        }).orElseThrow(() -> new IllegalArgumentException("PDF no encontrado"));
    }

    @Transactional
    public void marcarComoValidado(Long id, Boolean validado) {
        pdfRepository.findById(id).ifPresent(pdf -> {
            pdf.setValidadoCalidad(validado);
            pdfRepository.save(pdf);
        });
    }

    @Transactional
    public void incrementarUsos(Long id) {
        pdfRepository.findById(id).ifPresent(pdf -> {
            pdf.incrementarUsos();
            pdfRepository.save(pdf);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        pdfRepository.deleteById(id);
    }
}