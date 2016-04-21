package com.neptune.templates.microservice.service;

import java.util.List;

import javax.ws.rs.core.GenericEntity;

import com.neptune.templates.microservice.dao.DAOTemplate;
import com.neptune.templates.microservice.domain.DomainTemplate;

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