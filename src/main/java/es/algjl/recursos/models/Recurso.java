package es.algjl.recursos.models;

import es.algjl.recursos.models.enums.TipoRecurso;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "recursos")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "validado_calidad")
    private Boolean validadoCalidad = false;

    @Column(name = "contador_usos")
    private Long contadorUsos = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licencia_id")
    private LicenciaCC licenciaCC;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso", nullable = false)
    private TipoRecurso tipoRecurso;

    @Column(length = 200)
    private String nombre;

    public void incrementarUsos() {
        this.contadorUsos++;
    }
}