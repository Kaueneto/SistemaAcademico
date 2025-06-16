package org.example.sysacademico.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javafx.beans.property.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer cargaHoraria;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Disciplina> disciplinas = new HashSet<>();

    public Curso(Long id, String nome, Integer cargaHoraria, Set<Disciplina> disciplinas) {
        this.id = id;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.disciplinas = disciplinas != null ? disciplinas : new HashSet<>();
    }

    public LongProperty idProperty() {
        return new SimpleLongProperty(id);
    }

    public StringProperty nomeProperty() {
        return new SimpleStringProperty(nome);
    }

    public IntegerProperty cargaHorariaProperty() {
        return new SimpleIntegerProperty(cargaHoraria);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Curso curso = (Curso) o;
        return Objects.equals(id, curso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome;
    }
}