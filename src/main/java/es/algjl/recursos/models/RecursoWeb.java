package es.algjl.recursos.models;

import es.algjl.recursos.models.enums.TipoRecurso;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "recursos_web")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class RecursoWeb extends Recurso {

    @Column(name = "direccion_web", nullable = false, length = 500)
    private String direccionWeb;

    @PrePersist
    protected void onCreate() {
        setTipoRecurso(TipoRecurso.RECURSO_WEB);
    }
}