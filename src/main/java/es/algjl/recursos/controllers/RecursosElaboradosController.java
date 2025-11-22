package es.algjl.recursos.controllers;

import es.algjl.recursos.models.*;
import es.algjl.recursos.models.enums.*;
import es.algjl.recursos.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recursos-elaborados")
@RequiredArgsConstructor
public class RecursosElaboradosController {

    private final RecursoElaboradoService recursoElaboradoService;
    private final UsuarioService usuarioService;
    private final LicenciaCCService licenciaCCService;
    private final PdfGeneratorService pdfGeneratorService;
    
    // Services para buscar recursos
    private final ImagenService imagenService;
    private final PdfService pdfService;
    private final TextoFormateadoService textoFormateadoService;
    private final TextoPlanoService textoPlanoService;
    private final HojaCalculoService hojaCalculoService;
    private final RecursoWebService recursoWebService;
    private final EnlaceGoogleDocsService enlaceGoogleDocsService;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<RecursoElaborado> recursos = recursoElaboradoService.listarTodosPaginado(
                PageRequest.of(page, 10));

        model.addAttribute("recursos", recursos.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", recursos.getTotalPages());
        return "recursos-elaborados/listar";
    }

    @GetMapping("/visualizar/{id}")
    public String visualizar(@PathVariable Long id, Model model) {
        RecursoElaborado recurso = recursoElaboradoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));
        model.addAttribute("recurso", recurso);
        return "recursos-elaborados/visualizar-elaborado";
    }

    @PostMapping("/validar/{id}")
    @PreAuthorize("hasAnyRole('QUALITY', 'ADMIN')")
    public String validar(@PathVariable Long id, @RequestParam Boolean validado) {
        recursoElaboradoService.marcarComoValidado(id, validado);
        return "redirect:/recursos-elaborados";
    }

 

    @PostMapping("/guardar-borrador")
    @ResponseBody
    public ResponseEntity<?> guardarBorrador(
            @RequestBody Map<String, Object> datos,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            RecursoElaborado recurso = RecursoElaborado.builder()
                    .nombre((String) datos.get("nombre"))
                    .usuarioCreador(usuario)
                    .fechaCreacion(LocalDateTime.now())
                    .uso(TipoUso.valueOf((String) datos.get("uso")))
                    .creadoPara(CreadoPara.valueOf((String) datos.get("creadoPara")))
                    .materia(Materia.valueOf((String) datos.get("materia")))
                    .tipoTemplate(datos.get("template") != null ? 
                            TipoTemplate.valueOf(((String) datos.get("template")).toUpperCase().replace("-", "")) : null)
                    .datosComposicion((String) datos.get("composicion"))
                    .validadoCalidad(false)
                    .recursosUsados(new ArrayList<>())
                    .build();

            	
            
            if (datos.get("licenciaId") != null && !datos.get("licenciaId").toString().isEmpty()) {
                Long licenciaId = Long.parseLong(datos.get("licenciaId").toString());
                licenciaCCService.buscarPorId(licenciaId).ifPresent(recurso::setLicencia);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> recursosData = (List<Map<String, Object>>) datos.get("recursos");
            if (recursosData != null) {
                for (Map<String, Object> recursoData : recursosData) {
                    Long id = Long.parseLong(recursoData.get("id").toString());
                    String tipo = (String) recursoData.get("tipo");
                    
                    Recurso recursoEncontrado = buscarRecursoPorTipoId(tipo, id);
                    if (recursoEncontrado != null) {
                        recurso.getRecursosUsados().add(recursoEncontrado);
                        recursoEncontrado.incrementarUsos();
                    }
                }
            }

            RecursoElaborado guardado = recursoElaboradoService.crear(recurso);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mensaje", "Borrador guardado correctamente",
                    "id", guardado.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/generar-pdf")
    public ResponseEntity<?> generarPDF(
            @RequestBody Map<String, Object> datos,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            ResponseEntity<?> respuestaBorrador = guardarBorrador(datos, userDetails);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) respuestaBorrador.getBody();
            if (body == null || !(Boolean) body.get("success")) {
                return respuestaBorrador;
            }

            Long recursoId = ((Number) body.get("id")).longValue();
            RecursoElaborado recurso = recursoElaboradoService.buscarPorId(recursoId)
                    .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));

            byte[] pdfBytes = pdfGeneratorService.generarPDFDesdeRecurso(recurso);
            
            String rutaPdf = "./recursos-generados/" + recurso.getNombre() + ".pdf";
            recursoElaboradoService.guardarRutaPdf(recursoId, rutaPdf);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", recurso.getNombre() + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    private Recurso buscarRecursoPorTipoId(String tipo, Long id) {
        return switch (tipo.toUpperCase()) {
            case "IMAGEN" -> imagenService.buscarPorId(id).orElse(null);
            case "PDF" -> pdfService.buscarPorId(id).orElse(null);
            case "TEXTO_FORMATEADO" -> textoFormateadoService.buscarPorId(id).orElse(null);
            case "TEXTO_PLANO" -> textoPlanoService.buscarPorId(id).orElse(null);
            case "HOJA_CALCULO" -> hojaCalculoService.buscarPorId(id).orElse(null);
            case "RECURSO_WEB" -> recursoWebService.buscarPorId(id).orElse(null);
            case "ENLACE_GOOGLE_DOCS" -> enlaceGoogleDocsService.buscarPorId(id).orElse(null);
            default -> null;
        };
    }
}