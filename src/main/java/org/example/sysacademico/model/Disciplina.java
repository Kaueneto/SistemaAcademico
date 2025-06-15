package org.example.sysacademico.model;

import java.util.HashSet;
import java.util.Set;

public class Disciplina {


    private Long id;

    private String nome;
    private String descricao;


    private Curso curso;



    private Set<Professor> professores = new HashSet<>();

}
