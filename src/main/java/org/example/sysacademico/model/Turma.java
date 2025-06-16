package org.example.sysacademico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javafx.beans.property.*;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String semestre;
    private String horario;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    public Turma(String semestre, String horario, Disciplina disciplina, Professor professor) {
        this.semestre = semestre;
        this.horario = horario;
        this.disciplina = disciplina;
        this.professor = professor;
    }

    public LongProperty idProperty() {
        return new SimpleLongProperty(id);
    }

    public StringProperty semestreProperty() {
        return new SimpleStringProperty(semestre);
    }

    public StringProperty horarioProperty() {
        return new SimpleStringProperty(horario);
    }

    public StringProperty disciplinaProperty() {
        return new SimpleStringProperty(disciplina == null ? "" : disciplina.getNome());
    }

    public StringProperty professorProperty() {
        return new SimpleStringProperty(professor == null ? "" : professor.getNome());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turma that = (Turma) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return semestre + " - " + (disciplina == null ? "" : disciplina.getNome());
    }
}