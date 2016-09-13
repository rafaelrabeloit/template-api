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

    DAOTemplate<T> getDAO();

    GenericEntity<List<T>> page(int n, int start);

    T create(T entity);

    T retrieve(T entity);

    T update(T entity);

    T delete(T entity);
}