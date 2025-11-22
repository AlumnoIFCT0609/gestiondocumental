package es.algjl.recursos.models;

import es.algjl.recursos.models.enums.TipoRecurso;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "textos_planos")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class TextoPlano extends Recurso {

    @Column(name = "directorio_origen", nullable = false, length = 500)
    private String directorioOrigen;

    @PrePersist
    protected void onCreate() {
        setTipoRecurso(TipoRecurso.TEXTO_PLANO);
    }
}
