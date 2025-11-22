package es.algjl.recursos.models;

import es.algjl.recursos.models.enums.TipoRecurso;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "enlaces_google_docs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class EnlaceGoogleDocs extends Recurso {

    @Column(name = "direccion_web", nullable = false, length = 500)
    private String direccionWeb;

    @Column(name = "cuenta_acceso", length = 100)
    private String cuentaAcceso;

    @Column(name = "password_acceso", length = 100)
    private String passwordAcceso;

    @PrePersist
    protected void onCreate() {
        setTipoRecurso(TipoRecurso.ENLACE_GOOGLE_DOCS);
    }
}
