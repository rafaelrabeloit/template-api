package com.neptune.api.template.storage.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.GenericEntity;

import com.neptune.api.template.dao.DAOTemplate;
import com.neptune.api.template.dao.DAOTemplateImpl;
import com.neptune.api.template.dao.Filtering;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.domain.DomainTemplate;;

/**
 * 
 * @author Rafael Rabelo
 *
 * @param <T>
 *            The Domain class that this DAO will manage
 */
@JPAStorage
public abstract class StorageTemplateJPA<T extends DomainTemplate>
        extends DAOTemplateImpl<T> implements DAOTemplate<T> {

    /**
     * Ensure that this is called, because it sets the parameter for reflection!
     */
    public StorageTemplateJPA() {
        super();
    }

    public abstract EntityManager getEntityManager();

    /**
     * This can be used safely
     * 
     * @param maxResults
     *            max results for the query
     * @param offset
     *            the starting point for this query
     * @return a paginated list of items
     */
    @Override
    public GenericEntity<List<T>> page(Integer maxResults, Integer offset) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(getPersistentClass());
        Root<T> from = criteria.from(getPersistentClass());

        TypedQuery<T> typedQuery = getEntityManager()
                .createQuery(this.composeCriteria(from, criteria, builder));

        List<T> resultlist = typedQuery.setFirstResult(offset)
                .setMaxResults(maxResults).getResultList();

        GenericEntity<List<T>> result = new GenericEntity<List<T>>(resultlist,
                getType());

        return result;
    }

    /**
     * Read in CRUD Must be exposed to identify resources
     * 
     * @param entity
     *            Pseudo-Entity to be used as param to retrieve the full entity
     * @return entity that has the specified resourceId
     */
    @Override
    public T retrieve(T entity) {
        T e = getEntityManager().find(getPersistentClass(), entity.getId());
//        getEntityManager().refresh(entity);
        return e;
    }

    /**
     * Create in CRUD
     */
    @Override
    public T create(T entity) {
        try {
            getEntityManager().getTransaction().begin();
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            getEntityManager().persist(entity);
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
        }
        return entity;
    }

    /**
     * Delete in CRUD
     */
    @Override
    public T delete(T entity) {
        try {
            getEntityManager().getTransaction().begin();
            entity = getEntityManager().find(getPersistentClass(),
                    entity.getId());
            getEntityManager().remove(entity);
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
        }
        return entity;
    }

    /**
     * Update in CRUD
     */
    @Override
    public T update(T entity) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(entity);
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
        }
        return entity;
    }

    public CriteriaQuery<T> composeCriteria(Root<T> from,
            CriteriaQuery<T> criteria, CriteriaBuilder builder) {
        CriteriaQuery<T> query = criteria.select(from);

        List<Order> orders = new ArrayList<>();
        for (Ordering o : this.getOrders()) {
            if (o.getDirection() == Ordering.Direction.DESC) {
                orders.add(builder.desc(from.get(o.getField())));
            }
        }
        query = query.orderBy(orders);

        Predicate restrictions = builder.and();
        for (Filtering f : this.getFilters()) {
            if (f.getOperation() == Filtering.Operation.EQUAL) {
                restrictions = builder.and(restrictions,
                        builder.equal(from.get(f.getProperty()), f.getValue()));
            }
        }
        query = query.where(restrictions);
        return query;

    }
}
