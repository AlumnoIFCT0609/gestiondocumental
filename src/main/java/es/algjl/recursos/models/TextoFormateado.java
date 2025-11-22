package es.algjl.recursos.models;

import es.algjl.recursos.models.enums.TipoRecurso;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "textos_formateados")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class TextoFormateado extends Recurso {

    @Column(name = "directorio_origen", nullable = false, length = 500)
    private String directorioOrigen;

    @PrePersist
    protected void onCreate() {
        setTipoRecurso(TipoRecurso.TEXTO_FORMATEADO);
    }
}