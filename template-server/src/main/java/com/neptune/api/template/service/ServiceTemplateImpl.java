package com.neptune.api.template.service;

import java.util.List;

import javax.ws.rs.core.GenericEntity;

import com.neptune.api.template.dao.DAOTemplate;
import com.neptune.api.template.domain.DomainTemplate;

/**
 * 
 * @author Rafael Rabelo
 *
 * @param <T>
 *            The Domain class that this Service will manage
 */
public abstract class ServiceTemplateImpl<T extends DomainTemplate>
        implements ServiceTemplate<T> {

    @Override
    public abstract DAOTemplate<T> getDAO();

    public GenericEntity<List<T>> page(int n, int start) {
        return this.getDAO().page(n, start);
    }

    public T create(T entity) {
        return this.getDAO().create(entity);
    }

    public T retrieve(T entity) {
        return this.getDAO().retrieve(entity);
    }

    public T update(T entity) {
        return this.getDAO().update(entity);
    }

    public T delete(T entity) {
        return this.getDAO().delete(entity);
    }

}
