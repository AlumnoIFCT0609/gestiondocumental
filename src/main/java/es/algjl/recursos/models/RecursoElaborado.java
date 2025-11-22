package es.algjl.recursos.models;

import es.algjl.recursos.models.enums.CreadoPara;
import es.algjl.recursos.models.enums.Materia;
import es.algjl.recursos.models.enums.TipoTemplate;
import es.algjl.recursos.models.enums.TipoUso;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recursos_elaborados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecursoElaborado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 200)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "validado_calidad")
    private Boolean validadoCalidad = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "creado_para")
    private CreadoPara creadoPara;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_uso")
    private TipoUso uso;

    @Enumerated(EnumType.STRING)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licencia_id")
    private LicenciaCC licencia;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "recursos_elaborados_recursos",
        joinColumns = @JoinColumn(name = "recurso_elaborado_id"),
        inverseJoinColumns = @JoinColumn(name = "recurso_id")
    )
    @Builder.Default
    private List<Recurso> recursosUsados = new ArrayList<>();

    @Column(name = "ruta_pdf_generado", length = 500)
    private String rutaPdfGenerado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_template")
    private TipoTemplate tipoTemplate;

    @Column(name = "datos_composicion", columnDefinition = "TEXT")
    private String datosComposicion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}