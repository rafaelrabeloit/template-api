package com.neptune.templates.microservice.service;

import java.util.List;

import javax.ws.rs.core.GenericEntity;

import com.neptune.templates.microservice.dao.DAOTemplate;
import com.neptune.templates.microservice.domain.DomainTemplate;

/**
 * 
 * @author Rafael Rabelo
 *
 * @param <T> The Domain class that this Service will manage
 */
public abstract class ServiceTemplateImpl<T extends DomainTemplate> implements ServiceTemplate<T> {

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

	public T modify(T entity) {
		return this.getDAO().modify(entity);
	}
}
