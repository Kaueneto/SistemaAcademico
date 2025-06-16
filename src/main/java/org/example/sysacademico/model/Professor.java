package org.example.sysacademico.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import org.example.sysacademico.model.Disciplina;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String formacao;

    @ManyToMany(mappedBy = "professores")
    private Set<Disciplina> disciplinas = new HashSet<>();

    // Para TableView do JavaFX (propriedades observáveis)
    public SimpleLongProperty idProperty() {
        return new SimpleLongProperty(id);
    }

    public SimpleStringProperty nomeProperty() {
        return new SimpleStringProperty(nome);
    }

    public SimpleStringProperty emailProperty() {
        return new SimpleStringProperty(email);
    }

    public SimpleStringProperty formacaoProperty() {
        return new SimpleStringProperty(formacao);
    }

    @Override
    public String toString() {
        return nome; // útil em ComboBox
    }
}
