package com.neptune.api.template.dao;

import java.util.List;

import javax.ws.rs.core.GenericEntity;

import com.neptune.api.template.domain.DomainTemplate;

/**
 * Basic Operations to be used or extends by other DAOs
 * 
 * @author Rafael Rabelo
 * 
 */
public interface DAOTemplate<T extends DomainTemplate> {

    GenericEntity<List<T>> page(final Integer maxResults, final Integer offset);

    T create(T entity);

    T retrieve(T entity);

    T update(T entity);

    T delete(T entity);

    public List<Filtering> getFilters();
    
    public List<Ordering> getOrders();
}