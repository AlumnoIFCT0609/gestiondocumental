package es.algjl.recursos.controllers;

import es.algjl.recursos.models.Usuario;
import es.algjl.recursos.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioService usuarioService;
    private final RecursoElaboradoService recursoElaboradoService;
    private final ImagenService imagenService;
    private final PdfService pdfService;
    private final LicenciaCCService licenciaCCService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("rol", usuario.getRol().name());

        // Estadísticas generales
        model.addAttribute("totalRecursosElaborados", recursoElaboradoService.listarTodos().size());
        model.addAttribute("totalImagenes", imagenService.listarTodas().size());
        model.addAttribute("totalPdfs", pdfService.listarTodos().size());
        model.addAttribute("totalLicencias", licenciaCCService.listarTodas().size());

        // Datos específicos por rol
        switch (usuario.getRol()) {
            case ADMIN:
                model.addAttribute("usuariosTotal", usuarioService.listarTodos().size());
                break;
            case CREATOR:
                model.addAttribute("misRecursos", recursoElaboradoService.buscarPorCreador(usuario));
                break;
            case QUALITY:
                model.addAttribute("recursosValidados", recursoElaboradoService.buscarValidados().size());
                break;
            case READER:
                model.addAttribute("recursosDisponibles", recursoElaboradoService.buscarValidados());
                break;
        }

        return "dashboard";
    }
}