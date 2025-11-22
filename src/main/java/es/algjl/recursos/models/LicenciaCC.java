package es.algjl.recursos.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "licencias_cc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenciaCC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la licencia es obligatorio")
    @Column(nullable = false, length = 100)
    private String licencia;

    @Column(length = 500)
    private String descripcion;

    @Column(length = 50)
    private String codigo;

    @Column(length = 100)
    private String propietario;
}