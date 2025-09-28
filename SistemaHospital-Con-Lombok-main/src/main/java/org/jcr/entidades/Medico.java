package org.jcr.entidades;

import org.jcr.enums.EspecialidadMedica;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode(callSuper = true, of = {"matricula"}) // Incluye Persona + matricula
@ToString(callSuper = true, of = {"matricula", "especialidad"})
@SuperBuilder

public class Medico extends Persona implements Serializable {
    @Setter
    private Long id;
    private final Matricula matricula;
    private final EspecialidadMedica especialidad;

    @Setter // Solo departamento es mutable
    private Departamento departamento;
    private final List<Cita> citas = new ArrayList<>();

    protected Medico(MedicoBuilder<?, ?> builder) {
        super(builder);
        this.matricula = new Matricula(builder.numeroMatricula);
        this.especialidad = Objects.requireNonNull(builder.especialidad, "La especialidad no puede ser nula");
    }

    public static abstract class MedicoBuilder<C extends Medico, B extends MedicoBuilder<C, B>> extends PersonaBuilder<C, B> {
        private String numeroMatricula;
        private EspecialidadMedica especialidad;

        public B numeroMatricula(String numeroMatricula) {
            this.numeroMatricula = numeroMatricula;
            return self();
        }

        public B especialidad(EspecialidadMedica especialidad) {
            this.especialidad = especialidad;
            return self();
        }
    }

    // MÉTODOS DE NEGOCIO
    public void addCita(Cita cita) {
        this.citas.add(cita);
    }

    // Getter personalizado para lista inmutable
    public List<Cita> getCitas() {
        return Collections.unmodifiableList(new ArrayList<>(citas));
    }
}