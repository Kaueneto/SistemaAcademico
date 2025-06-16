package org.example.sysacademico.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.sysacademico.model.Disciplina;
import org.example.sysacademico.utils.JPAUtil;

import java.util.List;

public class DisciplinaDAO {

    public void create(Disciplina d) {
        trans(em -> em.persist(d));
    }

    public void update(Disciplina d) {
        trans(em -> em.merge(d));
    }

    public void delete(Disciplina d) {
        trans(em -> {
            if (d == null || d.getId() == null) {
                throw new IllegalArgumentException("selecione uma Disciplina");
            }

            Disciplina managed = em.find(Disciplina.class, d.getId());
            if (managed == null) {
                throw new RuntimeException("Disciplina não encontrada");
            }
            em.remove(managed);


        });
    }


    public Disciplina findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Disciplina.class, id);
        } finally {
            em.close();
        }
    }

    public List<Disciplina> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.clear();
            TypedQuery<Disciplina> q = em.createQuery("SELECT d FROM Disciplina d", Disciplina.class);
            q.setHint("jakarta.persistence.cache.storeMode", "REFRESH");
            List<Disciplina> result = q.getResultList();
            System.out.println("Disciplinas retornadas por findAll: " + result.size());
            return result;
        } finally {
            em.close();
        }
    }

    private void trans(java.util.function.Consumer<EntityManager> action) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            System.out.println("Iniciando transação...");
            em.getTransaction().begin();
            action.accept(em);
            em.getTransaction().commit();
            System.out.println("Transação confirmada.");
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                System.out.println("Reverting transação devido a erro: " + ex.getMessage());
                em.getTransaction().rollback();
            }
            System.out.println("Erro na transação: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                System.out.println("EntityManager fechado.");
            }
        }
    }
}