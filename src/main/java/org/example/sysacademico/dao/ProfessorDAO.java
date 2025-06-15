package org.example.sysacademico.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.sysacademico.model.Professor;

import java.util.List;

public class ProfessorDAO {


    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("SysAcademicoPU");

    public void salvar(Professor professor) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(professor);
        em.getTransaction().commit();
        em.close();

    }

    public List<Professor> listarTodos() {
        EntityManager em = emf.createEntityManager();
        List<Professor> professor = em.createQuery("from Professor").getResultList();
        em.close();
        return professor;

    }










}
