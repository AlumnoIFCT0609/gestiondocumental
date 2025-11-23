package es.algjl.recursos.controllers;

import es.algjl.recursos.models.*;
import es.algjl.recursos.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/recursos")
@RequiredArgsConstructor
public class RecursosController {

    private final ImagenService imagenService;
    private final PdfService pdfService;
    private final TextoFormateadoService textoFormateadoService;
    private final TextoPlanoService textoPlanoService;
    private final HojaCalculoService hojaCalculoService;
    private final RecursoWebService recursoWebService;
    private final EnlaceGoogleDocsService enlaceGoogleDocsService;
    private final EscaneoRecursosService escaneoRecursosService;
    private final LicenciaCCService licenciaCCService;

    @GetMapping
    public String listar(Model model) {
        List<Recurso> todosRecursos = new ArrayList<>();
        todosRecursos.addAll(imagenService.listarTodas());
        todosRecursos.addAll(pdfService.listarTodos());
        todosRecursos.addAll(textoFormateadoService.listarTodos());
        todosRecursos.addAll(textoPlanoService.listarTodos());
        todosRecursos.addAll(hojaCalculoService.listarTodas());
        todosRecursos.addAll(recursoWebService.listarTodos());
        todosRecursos.addAll(enlaceGoogleDocsService.listarTodos());

        model.addAttribute("recursos", todosRecursos);
        return "recursos/listar";
    }

    @GetMapping("/visualizar/{tipo}/{id}")
    public String visualizar(@PathVariable String tipo, @PathVariable Long id, Model model) {
        Recurso recurso = obtenerRecursoPorTipoId(tipo, id);
        model.addAttribute("recurso", recurso);
        model.addAttribute("tipo", tipo);
        return "recursos/visualizar-recurso";
    }
    @GetMapping("/visualizar/archivo/{tipo}/{id}")
    public ResponseEntity<byte[]> visualizarArchivo(
            @PathVariable String tipo,
            @PathVariable Long id,
            Model model) throws IOException {

        Recurso recurso = obtenerRecursoPorTipoId(tipo, id);

        
        // Solo para IMAGEN y PDF
        if (recurso instanceof Imagen imagen) {

            Path ruta = Paths.get(imagen.getDirectorioOrigen());

            if (!Files.exists(ruta)) {
                return ResponseEntity.notFound().build();
            }

            byte[] datos = Files.readAllBytes(ruta);

            // Detectar MIME real (png, jpg, gif, etc.)
            String mime = Files.probeContentType(ruta);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.parseMediaType(mime))
                    .body(datos);
        }  if (recurso instanceof Pdf pdf) {
            Path ruta = Paths.get(pdf.getDirectorioOrigen());
            if (!Files.exists(ruta)) return ResponseEntity.notFound().build();

            byte[] datos = Files.readAllBytes(ruta);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(datos);
        }
     // TEXTO PLANO
        if (recurso instanceof TextoPlano textoPlano) {
        	Path ruta = Paths.get(textoPlano.getDirectorioOrigen());
            if (!Files.exists(ruta)) return ResponseEntity.notFound().build();

            byte[] datos = Files.readAllBytes(ruta);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(datos);
        }


        // TEXTO FORMATEADO (doc, odt ...)
        if (recurso instanceof TextoFormateado texto) {

            Path ruta = Paths.get(texto.getDirectorioOrigen());
            if (!Files.exists(ruta)) return ResponseEntity.notFound().build();

            byte[] datos = Files.readAllBytes(ruta);

            String nombre = ruta.toString().toLowerCase();
            String mime;

            if (nombre.endsWith(".odt"))
                mime = "application/vnd.oasis.opendocument.text";
            else if (nombre.endsWith(".docx"))
                mime = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            else if (nombre.endsWith(".doc"))
                mime = "application/msword";
            else if (nombre.endsWith(".rtf"))
                mime = "application/rtf";
            else if (nombre.endsWith(".html") || nombre.endsWith(".htm"))
                mime = "text/html";
           
            else
                mime = "application/octet-stream"; // por defecto

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.parseMediaType(mime))
                    .body(datos);
        }


        // HOJA DE CÁLCULO (.xlsx)
        if (recurso instanceof HojaCalculo hojaCalculo) {
        	Path ruta = Paths.get(hojaCalculo.getDirectorioOrigen());
            if (!Files.exists(ruta)) return ResponseEntity.notFound().build();

            byte[] datos = Files.readAllBytes(ruta);
            String mime = Files.probeContentType(ruta);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.parseMediaType(
                            mime != null ? mime : "application/octet-stream"
                    ))
                    .body(datos);
        }
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String fileUrl = baseUrl + "/recursos/visualizar/archivo/TEXTO_FORMATEADO/" + recurso.getId();
        String extension;
        if (recurso.getNombre() != null && recurso.getNombre().contains(".")) {
           extension = recurso.getNombre()
                               .substring(recurso.getNombre().lastIndexOf('.') + 1)
                               .toLowerCase();
        }
        
        
        model.addAttribute("fileUrl", fileUrl);

        // Si luego tienes clase PDF, se haría igual que arriba.
        return ResponseEntity.badRequest().build();
    }


    @GetMapping("/descargar/{tipo}/{id}")
    public ResponseEntity<byte[]> descargar(@PathVariable String tipo, @PathVariable Long id) {
        try {
            Recurso recurso = obtenerRecursoPorTipoId(tipo, id);
            String rutaArchivo = obtenerRutaArchivo(recurso);
            
            if (rutaArchivo == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(rutaArchivo);
            byte[] contenido = Files.readAllBytes(path);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(determinarMediaType(rutaArchivo));
            headers.setContentDispositionFormData("attachment", recurso.getNombre());

            return new ResponseEntity<>(contenido, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/escanear")
    @PreAuthorize("hasAnyRole('ADMIN', 'QUALITY')")
    public String escanear(RedirectAttributes redirectAttributes) {
        try {
            escaneoRecursosService.escanearDirectorio();
            redirectAttributes.addFlashAttribute("mensaje", "Escaneo completado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al escanear: " + e.getMessage());
        }
        return "redirect:/recursos";
    }

    @PostMapping("/validar/{tipo}/{id}")
    @PreAuthorize("hasAnyRole('QUALITY', 'ADMIN')")
    public String validarRecurso(@PathVariable String tipo, 
                                @PathVariable Long id,
                                @RequestParam Boolean validado,
                                RedirectAttributes redirectAttributes) {
        try {
            marcarValidacion(tipo, id, validado);
            redirectAttributes.addFlashAttribute("mensaje", "Recurso " + (validado ? "validado" : "marcado como no válido"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/recursos";
    }

    @GetMapping("/montar-formatear")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public String montarFormatear(Model model) {
        List<Recurso> recursosDisponibles = new ArrayList<>();
        
        // Agregar TODAS las imágenes (no solo validadas)
        recursosDisponibles.addAll(imagenService.listarTodas());
        
        // Agregar otros recursos
        recursosDisponibles.addAll(pdfService.listarTodos());
        recursosDisponibles.addAll(textoFormateadoService.listarTodos());
        recursosDisponibles.addAll(textoPlanoService.listarTodos());
        recursosDisponibles.addAll(hojaCalculoService.listarTodas());

        model.addAttribute("recursos", recursosDisponibles);
        model.addAttribute("licencias", licenciaCCService.listarTodas());
        return "recursos/montar-formatear";
    }
    private Recurso obtenerRecursoPorTipoId(String tipo, Long id) {
    	 // Normalización:
        //   "TEXTO_PLANO" → "texto-plano"
        //   "TEXTO_FORMATEADO" → "texto-formateado"
        //   "HOJA_CALCULO" → "hoja-calculo"
      
        tipo = tipo.toLowerCase().replace("_", "-");

        return switch (tipo.toLowerCase()) {
            case "imagen" -> imagenService.buscarPorId(id).orElseThrow();
            case "pdf" -> pdfService.buscarPorId(id).orElseThrow();
            case "texto-formateado" -> textoFormateadoService.buscarPorId(id).orElseThrow();
            case "texto-plano" -> textoPlanoService.buscarPorId(id).orElseThrow();
            case "hoja-calculo" -> hojaCalculoService.buscarPorId(id).orElseThrow();
            case "recurso-web" -> recursoWebService.buscarPorId(id).orElseThrow();
            case "enlace-google" -> enlaceGoogleDocsService.buscarPorId(id).orElseThrow();
            default -> throw new IllegalArgumentException("Tipo de recurso no válido");
        };
    }

    private String obtenerRutaArchivo(Recurso recurso) {
        if (recurso instanceof Imagen img) return img.getDirectorioOrigen();
        if (recurso instanceof Pdf pdf) return pdf.getDirectorioOrigen();
        if (recurso instanceof TextoFormateado txt) return txt.getDirectorioOrigen();
        if (recurso instanceof TextoPlano txt) return txt.getDirectorioOrigen();
        if (recurso instanceof HojaCalculo hoja) return hoja.getDirectorioOrigen();
        return null;
    }

    private void marcarValidacion(String tipo, Long id, Boolean validado) {
        switch (tipo.toLowerCase()) {
            case "imagen" -> imagenService.marcarComoValidada(id, validado);
            case "pdf" -> pdfService.marcarComoValidado(id, validado);
            case "texto-formateado" -> textoFormateadoService.marcarComoValidado(id, validado);
            case "texto-plano" -> textoPlanoService.marcarComoValidado(id, validado);
            case "hoja-calculo" -> hojaCalculoService.marcarComoValidada(id, validado);
            case "recurso-web" -> recursoWebService.marcarComoValidado(id, validado);
            case "enlace-google" -> enlaceGoogleDocsService.marcarComoValidado(id, validado);
        }
    }

    private MediaType determinarMediaType(String ruta) {
        String extension = ruta.substring(ruta.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}