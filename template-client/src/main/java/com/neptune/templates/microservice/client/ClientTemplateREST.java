package com.neptune.templates.microservice.client;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import com.neptune.templates.microservice.dao.DAOTemplate;
import com.neptune.templates.microservice.dao.DAOTemplateImpl;
import com.neptune.templates.microservice.domain.DomainTemplate;

/**
 * 
 * @author Rafael Rabelo
 *
 * @param <T> The Domain class that this DAO will manage
 */
@RESTClient
public abstract class ClientTemplateREST<T extends DomainTemplate> extends DAOTemplateImpl<T> implements DAOTemplate<T> {

	//TODO: This should be a singleton
	private Client client;
	//TODO: this I dont know
	protected WebTarget templateTarget;
	
	/**
	 * Ensure that this is called, because it sets the parameter for reflection!
	 */
	public ClientTemplateREST() {
		super();
		
		this.client = ClientBuilder.newClient();
	}

	protected abstract void postConstruct();
	
	public void initialize(String target, String path) {
		this.templateTarget = client.target(target).path(path);
	}
	
	public WebTarget getWebtarget() {
		return templateTarget;
	}

	/**
	 * This can be used safely
	 * TODO: limit maxResults!
	 * @param maxResults
	 * @param offset
	 * @return a paginated list of items
	 */
	@Override
	public GenericEntity<List<T>> page(Integer maxResults, Integer offset) {
		List<T> list = null;
		
		GenericEntity<List<T>> result = new GenericEntity<List<T>>(
				list, getType());
		
		return result;
	}
	
	/**
	 * Read in CRUD
	 * Must be exposed to identify resources
	 * @param resourceId
	 * @return entity that has the specified resourceId
	 */
	@Override
	public T retrieve(T entity) {
		WebTarget targetRequest = templateTarget.resolveTemplate("id", entity.getResourceId());
		
		entity = targetRequest.request(MediaType.APPLICATION_JSON_TYPE).
			get(this.getPersistentClass());
		
		return entity;
	}

	/**
	 * Create in CRUD
	 */
	@Override
    public T create(T entity) {
		WebTarget targetRequest = templateTarget.resolveTemplate("id", "");
		
		entity = targetRequest.request(MediaType.APPLICATION_JSON_TYPE).
				post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE), this.getPersistentClass());
			
		return entity;
	}
	
	/**
	 * Delete in CRUD
	 */
	@Override
	public T delete(T entity) {
		WebTarget targetRequest = templateTarget.resolveTemplate("id", entity.getResourceId());
		
		entity = targetRequest.request(MediaType.APPLICATION_JSON_TYPE).
				delete(this.getPersistentClass());
			
		return entity;
	}

	/**
	 * Update in CRUD
	 */
	@Override
	public T update(T entity) {
		WebTarget targetRequest = templateTarget.resolveTemplate("id", entity.getResourceId());
		
		entity = targetRequest.request(MediaType.APPLICATION_JSON_TYPE).
				put(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE), this.getPersistentClass());
			
		return entity;		
	}
	
	@Override
	public T modify(T entity) {
		// TODO Auto-generated method stub
		return null;
	}
}
