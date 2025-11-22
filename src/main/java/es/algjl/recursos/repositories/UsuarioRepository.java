package es.algjl.recursos.repositories;

import es.algjl.recursos.models.Usuario;
import es.algjl.recursos.models.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByRol(Rol rol);
    boolean existsByEmail(String email);
}