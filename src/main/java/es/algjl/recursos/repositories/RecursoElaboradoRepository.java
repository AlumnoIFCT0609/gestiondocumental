package es.algjl.recursos.repositories;

import es.algjl.recursos.models.RecursoElaborado;
import es.algjl.recursos.models.Usuario;
import es.algjl.recursos.models.enums.CreadoPara;
import es.algjl.recursos.models.enums.Materia;
import es.algjl.recursos.models.enums.TipoUso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoElaboradoRepository extends JpaRepository<RecursoElaborado, Long> {
    Page<RecursoElaborado> findAll(Pageable pageable);
    List<RecursoElaborado> findByUsuarioCreador(Usuario usuario);
    List<RecursoElaborado> findByUso(TipoUso uso);
    List<RecursoElaborado> findByCreadoPara(CreadoPara creadoPara);
    List<RecursoElaborado> findByMateria(Materia materia);
    List<RecursoElaborado> findByValidadoCalidad(Boolean validado);
    List<RecursoElaborado> findByNombreContainingIgnoreCase(String nombre);
}