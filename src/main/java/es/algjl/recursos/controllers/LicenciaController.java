package es.algjl.recursos.controllers;

import es.algjl.recursos.models.LicenciaCC;
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
@RequestMapping("/licencias")
@PreAuthorize("hasAnyRole('ADMIN', 'QUALITY')")
@RequiredArgsConstructor
public class LicenciaController {

    private final LicenciaCCService licenciaCCService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("licencias", licenciaCCService.listarTodas());
        return "licenciasCC/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("licencia", new LicenciaCC());
        return "licenciasCC/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        LicenciaCC licencia = licenciaCCService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Licencia no encontrada"));
        model.addAttribute("licencia", licencia);
        return "licenciasCC/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute LicenciaCC licencia,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "licenciasCC/form";
        }

        try {
            if (licencia.getId() == null) {
                licenciaCCService.crear(licencia);
                redirectAttributes.addFlashAttribute("mensaje", "Licencia creada correctamente");
            } else {
                licenciaCCService.actualizar(licencia.getId(), licencia);
                redirectAttributes.addFlashAttribute("mensaje", "Licencia actualizada correctamente");
            }
            return "redirect:/licencias";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "licenciasCC/form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            licenciaCCService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Licencia eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/licencias";
    }
}