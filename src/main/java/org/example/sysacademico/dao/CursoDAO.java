package org.example.sysacademico.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.sysacademico.model.Curso;
import org.example.sysacademico.utils.JPAUtil;

import java.util.List;

public class CursoDAO {

    public void create(Curso c) {
        trans(em -> em.persist(c));
    }

    public void update(Curso c) {
        trans(em -> em.merge(c));
    }

    public void delete(Curso c) {
        trans(em -> {
            System.out.println("Excluindo curso com ID: " + c.getId());
            Curso managed = em.contains(c) ? c : em.merge(c);
            em.remove(managed);
        });
    }

    public Curso findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Curso.class, id);
        } finally {
            em.close();
        }
    }

    public List<Curso> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Curso> q = em.createQuery("SELECT c FROM Curso c", Curso.class);
            List<Curso> result = q.getResultList();
            System.out.println("Cursos retornados por findAll: " + result.size());
            return result;
        } finally {
            em.close();
        }
    }

    private void trans(java.util.function.Consumer<EntityManager> action) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            action.accept(em);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.out.println("Erro na transação: " + ex.getMessage());
            throw ex;
        } finally {
            em.close();
        }
    }
}