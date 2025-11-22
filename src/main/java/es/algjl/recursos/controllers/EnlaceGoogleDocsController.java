package es.algjl.recursos.controllers;

import es.algjl.recursos.models.EnlaceGoogleDocs;
import es.algjl.recursos.services.EnlaceGoogleDocsService;
import es.algjl.recursos.services.LicenciaCCService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enlaces-google-docs")
@PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
@RequiredArgsConstructor
public class EnlaceGoogleDocsController {

    private final EnlaceGoogleDocsService enlaceGoogleDocsService;
    private final LicenciaCCService licenciaCCService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("enlaces", enlaceGoogleDocsService.listarTodos());
        return "enlaces-google-docs/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("enlace", new EnlaceGoogleDocs());
        model.addAttribute("licencias", licenciaCCService.listarTodas());
        return "enlaces-google-docs/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        EnlaceGoogleDocs enlace = enlaceGoogleDocsService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Enlace no encontrado"));
        model.addAttribute("enlace", enlace);
        model.addAttribute("licencias", licenciaCCService.listarTodas());
        return "enlaces-google-docs/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute EnlaceGoogleDocs enlace,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("licencias", licenciaCCService.listarTodas());
            return "enlaces-google-docs/form";
        }

        try {
            if (enlace.getId() == null) {
                enlaceGoogleDocsService.crear(enlace);
                redirectAttributes.addFlashAttribute("mensaje", "Enlace creado correctamente");
            } else {
                enlaceGoogleDocsService.actualizar(enlace.getId(), enlace);
                redirectAttributes.addFlashAttribute("mensaje", "Enlace actualizado correctamente");
            }
            return "redirect:/enlaces-google-docs";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("licencias", licenciaCCService.listarTodas());
            return "enlaces-google-docs/form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            enlaceGoogleDocsService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Enlace eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/enlaces-google-docs";
    }
}