package org.example.sysacademico.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.sysacademico.model.Turma;
import org.example.sysacademico.utils.JPAUtil;

import java.util.List;

public class TurmaDAO {

    public void create(Turma t) {
        trans(em -> em.persist(t));
    }

    public void update(Turma t) {
        trans(em -> em.merge(t));
    }

    public void delete(Turma t) {
        trans(em -> em.remove(em.contains(t) ? t : em.merge(t)));
    }

    public Turma findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try { return em.find(Turma.class, id); }
        finally { em.close(); }
    }

    public List<Turma> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Turma> q =
                    em.createQuery("SELECT t FROM Turma t", Turma.class);
            return q.getResultList();
        } finally { em.close(); }
    }

    private void trans(java.util.function.Consumer<EntityManager> action) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            action.accept(em);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
