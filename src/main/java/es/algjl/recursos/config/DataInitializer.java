package es.algjl.recursos.config;

import es.algjl.recursos.models.LicenciaCC;
import es.algjl.recursos.models.Usuario;
import es.algjl.recursos.models.enums.Rol;
import es.algjl.recursos.repositories.LicenciaCCRepository;
import es.algjl.recursos.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final LicenciaCCRepository licenciaCCRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeUsuarios();
        initializeLicencias();
    }

    private void initializeUsuarios() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .email("admin@gestion.com")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .fechaCreacion(LocalDateTime.now())
                    .ultimoAcceso(LocalDateTime.now())
                    .build();

            usuarioRepository.save(admin);
            log.info("Usuario administrador creado: admin@gestion.com / admin123");
        }
    }

    private void initializeLicencias() {
        if (licenciaCCRepository.count() == 0) {
            licenciaCCRepository.save(LicenciaCC.builder()
                    .licencia("CC0 - Dominio Público")
                    .descripcion("Sin derechos reservados, dedicado al dominio público")
                    .codigo("CC0")
                    .propietario("Creative Commons")
                    .build());

            licenciaCCRepository.save(LicenciaCC.builder()
                    .licencia("CC BY - Atribución")
                    .descripcion("Permite usar, distribuir y crear obras derivadas nombrando al autor")
                    .codigo("CC-BY")
                    .propietario("Creative Commons")
                    .build());

            licenciaCCRepository.save(LicenciaCC.builder()
                    .licencia("CC BY-SA - Atribución-CompartirIgual")
                    .descripcion("Igual que CC BY pero las obras derivadas deben usar la misma licencia")
                    .codigo("CC-BY-SA")
                    .propietario("Creative Commons")
                    .build());

            licenciaCCRepository.save(LicenciaCC.builder()
                    .licencia("CC BY-ND - Atribución-SinDerivadas")
                    .descripcion("Permite redistribuir pero no crear obras derivadas")
                    .codigo("CC-BY-ND")
                    .propietario("Creative Commons")
                    .build());

            licenciaCCRepository.save(LicenciaCC.builder()
                    .licencia("Copyright - Todos los derechos reservados")
                    .descripcion("Uso restringido, requiere permiso del autor")
                    .codigo("COPYRIGHT")
                    .propietario("Propietario del recurso")
                    .build());

            log.info("Licencias Creative Commons inicializadas");
        }
    }
}