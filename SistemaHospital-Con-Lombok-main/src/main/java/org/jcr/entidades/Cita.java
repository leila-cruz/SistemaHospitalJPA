package org.jcr.entidades;

import org.jcr.enums.EstadoCita;
import org.jcr.excepciones.CitaException;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Getter // Solo getters automáticos para campos final
@ToString(exclude = {"paciente","medico","sala"}) // ToString automático está bien
@Builder

public class Cita implements Serializable {
    @Setter
    private Long id;
    private final Paciente paciente;
    private final Medico medico;
    private final Sala sala;
    private final LocalDateTime fechaHora;
    private final BigDecimal costo;

    @Setter // Solo estos 2 campos necesitan setters
    private EstadoCita estado;

    @Setter
    private String observaciones;

    // BUILDER
    private Cita(CitaBuilder builder) {
        this.paciente = Objects.requireNonNull(builder.paciente, "El paciente no puede ser nulo");
        this.medico = Objects.requireNonNull(builder.medico, "El médico no puede ser nulo");
        this.sala = Objects.requireNonNull(builder.sala, "La sala no puede ser nula");
        this.fechaHora = Objects.requireNonNull(builder.fechaHora, "La fecha y hora no pueden ser nulas");
        this.costo = Objects.requireNonNull(builder.costo, "El costo no puede ser nulo");
        this.estado = builder.estado != null ? builder.estado : EstadoCita.PROGRAMADA;
        this.observaciones = builder.observaciones != null ? builder.observaciones : "";
    }

    public static class CitaBuilder {
        private Paciente paciente;
        private Medico medico;
        private Sala sala;
        private LocalDateTime fechaHora;
        private BigDecimal costo;
        private EstadoCita estado;
        private String observaciones;

        public CitaBuilder paciente(Paciente paciente) {
            this.paciente = paciente;
            return this;
        }

        public CitaBuilder medico(Medico medico) {
            this.medico = medico;
            return this;
        }

        public CitaBuilder sala(Sala sala) {
            this.sala = sala;
            return this;
        }

        public CitaBuilder fechaHora(LocalDateTime fechaHora) {
            this.fechaHora = fechaHora;
            return this;
        }

        public CitaBuilder costo(BigDecimal costo) {
            this.costo = costo;
            return this;
        }

        public CitaBuilder estado(EstadoCita estado) {
            this.estado = estado;
            return this;
        }

        public CitaBuilder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public Cita build() {
            return new Cita(this);
        }
    }

    // MÉTODOS CSV
    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                paciente.getDni(),
                medico.getDni(),
                sala.getNumero(),
                fechaHora.toString(),
                costo.toString(),
                estado.name(),
                observaciones.replaceAll(",", ";"));
    }

    public static Cita fromCsvString(String csvString,
                                     Map<String, Paciente> pacientes,
                                     Map<String, Medico> medicos,
                                     Map<String, Sala> salas) throws CitaException {
        String[] values = csvString.split(",");
        if (values.length != 7) {
            throw new CitaException("Formato de CSV inválido para Cita: " + csvString);
        }

        String dniPaciente = values[0];
        String dniMedico = values[1];
        String numeroSala = values[2];
        LocalDateTime fechaHora = LocalDateTime.parse(values[3]);
        BigDecimal costo = new BigDecimal(values[4]);
        EstadoCita estado = EstadoCita.valueOf(values[5]);
        String observaciones = values[6].replaceAll(";", ",");

        Paciente paciente = pacientes.get(dniPaciente);
        Medico medico = medicos.get(dniMedico);
        Sala sala = salas.get(numeroSala);

        if (paciente == null) {
            throw new CitaException("Paciente no encontrado: " + dniPaciente);
        }
        if (medico == null) {
            throw new CitaException("Médico no encontrado: " + dniMedico);
        }
        if (sala == null) {
            throw new CitaException("Sala no encontrada: " + numeroSala);
        }

        return Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .sala(sala)
                .fechaHora(fechaHora)
                .costo(costo)
                .estado(estado)
                .observaciones(observaciones)
                .build();
    }

    // SETTERS PERSONALIZADOS CON VALIDACIÓN
    public void setEstado(EstadoCita estado) {
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones != null ? observaciones : "";
    }
}
