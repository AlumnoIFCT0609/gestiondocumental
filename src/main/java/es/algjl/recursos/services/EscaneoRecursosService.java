package es.algjl.recursos.services;

import es.algjl.recursos.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EscaneoRecursosService {

    @Value("${app.recursos.directorio-base}")
    private String directorioBase;

    private final ImagenService imagenService;
    private final PdfService pdfService;
    private final TextoFormateadoService textoFormateadoService;
    private final TextoPlanoService textoPlanoService;
    private final HojaCalculoService hojaCalculoService;

    private static final List<String> EXTENSIONES_IMAGEN = Arrays.asList("png", "jpg", "jpeg", "gif");
    private static final List<String> EXTENSIONES_PDF = List.of("pdf");
    private static final List<String> EXTENSIONES_TEXTO_FORMATEADO = Arrays.asList("doc", "docx", "odt");
    private static final List<String> EXTENSIONES_TEXTO_PLANO = List.of("txt");
    private static final List<String> EXTENSIONES_HOJA_CALCULO = Arrays.asList("xls", "xlsx", "ods");

    @Scheduled(cron = "${app.scheduler.escaneo-recursos}")
    public void escanearDirectorioAutomatico() {
        log.info("Iniciando escaneo automático de recursos (cada 24h)");
        escanearDirectorio();
    }

    public void escanearDirectorio() {
        try {
            Path directorioPath = Paths.get(directorioBase);
            
            if (!Files.exists(directorioPath)) {
                Files.createDirectories(directorioPath);
                log.info("Directorio base creado: {}", directorioBase);
            }

            try (Stream<Path> paths = Files.walk(directorioPath)) {
                paths.filter(Files::isRegularFile)
                     .forEach(this::procesarArchivo);
            }

            log.info("Escaneo de recursos completado");
        } catch (IOException e) {
            log.error("Error al escanear directorio: {}", e.getMessage(), e);
        }
    }

    private void procesarArchivo(Path archivo) {
        String extension = FilenameUtils.getExtension(archivo.toString()).toLowerCase();
        String nombreArchivo = archivo.getFileName().toString();
        String rutaCompleta = archivo.toAbsolutePath().toString();

        try {
            if (EXTENSIONES_IMAGEN.contains(extension)) {
                crearImagenSiNoExiste(nombreArchivo, rutaCompleta);
            } else if (EXTENSIONES_PDF.contains(extension)) {
                crearPdfSiNoExiste(nombreArchivo, rutaCompleta);
            } else if (EXTENSIONES_TEXTO_FORMATEADO.contains(extension)) {
                crearTextoFormateadoSiNoExiste(nombreArchivo, rutaCompleta);
            } else if (EXTENSIONES_TEXTO_PLANO.contains(extension)) {
                crearTextoPlanoSiNoExiste(nombreArchivo, rutaCompleta);
            } else if (EXTENSIONES_HOJA_CALCULO.contains(extension)) {
                crearHojaCalculoSiNoExiste(nombreArchivo, rutaCompleta);
            }
        } catch (Exception e) {
            log.warn("Error al procesar archivo {}: {}", nombreArchivo, e.getMessage());
        }
    }

    private void crearImagenSiNoExiste(String nombre, String ruta) {
        List<Imagen> existentes = imagenService.buscarPorNombre(nombre);
        if (existentes.isEmpty()) {
            Imagen imagen = Imagen.builder()
                    .nombre(nombre)
                    .directorioOrigen(ruta)
                    .validadoCalidad(false)
                    .contadorUsos(0L)
                    .build();
            imagenService.crear(imagen);
            log.debug("Imagen creada: {}", nombre);
        }
    }

    private void crearPdfSiNoExiste(String nombre, String ruta) {
        List<Pdf> existentes = pdfService.buscarPorNombre(nombre);
        if (existentes.isEmpty()) {
            Pdf pdf = Pdf.builder()
                    .nombre(nombre)
                    .directorioOrigen(ruta)
                    .validadoCalidad(false)
                    .contadorUsos(0L)
                    .build();
            pdfService.crear(pdf);
            log.debug("PDF creado: {}", nombre);
        }
    }

    private void crearTextoFormateadoSiNoExiste(String nombre, String ruta) {
        List<TextoFormateado> existentes = textoFormateadoService.buscarPorNombre(nombre);
        if (existentes.isEmpty()) {
            TextoFormateado texto = TextoFormateado.builder()
                    .nombre(nombre)
                    .directorioOrigen(ruta)
                    .validadoCalidad(false)
                    .contadorUsos(0L)
                    .build();
            textoFormateadoService.crear(texto);
            log.debug("Texto formateado creado: {}", nombre);
        }
    }

    private void crearTextoPlanoSiNoExiste(String nombre, String ruta) {
        List<TextoPlano> existentes = textoPlanoService.buscarPorNombre(nombre);
        if (existentes.isEmpty()) {
            TextoPlano texto = TextoPlano.builder()
                    .nombre(nombre)
                    .directorioOrigen(ruta)
                    .validadoCalidad(false)
                    .contadorUsos(0L)
                    .build();
            textoPlanoService.crear(texto);
            log.debug("Texto plano creado: {}", nombre);
        }
    }

    private void crearHojaCalculoSiNoExiste(String nombre, String ruta) {
        List<HojaCalculo> existentes = hojaCalculoService.listarTodas().stream()
                .filter(h -> h.getNombre().equalsIgnoreCase(nombre))
                .toList();
        if (existentes.isEmpty()) {
            HojaCalculo hoja = HojaCalculo.builder()
                    .nombre(nombre)
                    .directorioOrigen(ruta)
                    .validadoCalidad(false)
                    .contadorUsos(0L)
                    .build();
            hojaCalculoService.crear(hoja);
            log.debug("Hoja de cálculo creada: {}", nombre);
        }
    }
}