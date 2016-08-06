package com.neptune.api.template.service;

import java.util.List;

import javax.ws.rs.core.GenericEntity;

import com.neptune.api.template.dao.DAOTemplate;
import com.neptune.api.template.domain.DomainTemplate;

/**
 * Service Layer
 * 
 * @author Rafael Rabelo
 * 
 */
public interface ServiceTemplate<T extends DomainTemplate> {

    public DAOTemplate<T> getDAO();

    public GenericEntity<List<T>> page(int n, int start);

    public T create(T entity);

    public T retrieve(T entity);

    public T update(T entity);

    public T delete(T entity);

    public T modify(T entity);
}