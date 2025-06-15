package org.example.sysacademico.model;

import java.util.HashSet;
import java.util.Set;

public class Professor {


    private Long id;

    private String nome;
    private String email;
    private String formacao;

    private Set<Disciplina> disciplinas = new HashSet<>();

}
