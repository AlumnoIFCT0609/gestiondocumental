package es.algjl.recursos.services;

import es.algjl.recursos.models.HojaCalculo;
import es.algjl.recursos.repositories.HojaCalculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HojaCalculoService {

    private final HojaCalculoRepository hojaCalculoRepository;

    @Transactional(readOnly = true)
    public List<HojaCalculo> listarTodas() {
        return hojaCalculoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<HojaCalculo> buscarPorId(Long id) {
        return hojaCalculoRepository.findById(id);
    }

    @Transactional
    public HojaCalculo crear(HojaCalculo hoja) {
        log.info("Creando hoja de cálculo: {}", hoja.getNombre());
        return hojaCalculoRepository.save(hoja);
    }

    @Transactional
    public HojaCalculo actualizar(Long id, HojaCalculo hojaActualizada) {
        return hojaCalculoRepository.findById(id).map(hoja -> {
            hoja.setNombre(hojaActualizada.getNombre());
            hoja.setDescripcion(hojaActualizada.getDescripcion());
            hoja.setLicenciaCC(hojaActualizada.getLicenciaCC());
            return hojaCalculoRepository.save(hoja);
        }).orElseThrow(() -> new IllegalArgumentException("Hoja de cálculo no encontrada"));
    }

    @Transactional
    public void marcarComoValidada(Long id, Boolean validada) {
        hojaCalculoRepository.findById(id).ifPresent(hoja -> {
            hoja.setValidadoCalidad(validada);
            hojaCalculoRepository.save(hoja);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        hojaCalculoRepository.deleteById(id);
    }
}