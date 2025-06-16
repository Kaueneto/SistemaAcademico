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
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToMany
    @JoinTable(name = "disciplina_professor",
            joinColumns = @JoinColumn(name = "disciplina_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id"))
    private Set<Professor> professores = new HashSet<>();

    public Disciplina(Long id, String nome, String descricao, Curso curso, Set<Professor> professores) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.curso = curso;
        this.professores = professores != null ? professores : new HashSet<>();
    }

    public LongProperty idProperty() {
        return new SimpleLongProperty(id);
    }

    public StringProperty nomeProperty() {
        return new SimpleStringProperty(nome);
    }

    public StringProperty descricaoProperty() {
        return new SimpleStringProperty(descricao);
    }

    public StringProperty cursoProperty() {
        return new SimpleStringProperty(curso == null ? "" : curso.getNome());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disciplina that = (Disciplina) o;
        return Objects.equals(id, that.id);
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