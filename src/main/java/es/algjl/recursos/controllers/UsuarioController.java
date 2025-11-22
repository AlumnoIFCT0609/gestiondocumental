package es.algjl.recursos.controllers;

import es.algjl.recursos.models.Usuario;
import es.algjl.recursos.models.enums.Rol;
import es.algjl.recursos.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("esNuevo", true);
        return "usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("esNuevo", false);
        return "usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario,
                         BindingResult result,
                         @RequestParam(required = false) String nuevaPassword,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("esNuevo", usuario.getId() == null);
            return "usuarios/form";
        }

        try {
            if (usuario.getId() == null) {
                usuarioService.crear(usuario);
                redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
            } else {
                usuarioService.actualizar(usuario.getId(), usuario);
                if (nuevaPassword != null && !nuevaPassword.isBlank()) {
                    usuarioService.cambiarPassword(usuario.getId(), nuevaPassword);
                }
                redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
            }
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", Rol.values());
            model.addAttribute("esNuevo", usuario.getId() == null);
            return "usuarios/form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }
}