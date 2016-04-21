package com.neptune.templates.microservice.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.neptune.templates.microservice.domain.DomainTemplate;

/**
 * 
 * @author Rafael Rabelo
 *
 * @param <T> The Domain class that this DAO will manage
 */
public abstract class DAOTemplateImpl<T extends DomainTemplate> implements DAOTemplate<T> {

	private Type genType;
	private ParameterizedListType param;

	private List<Filter> filters = new LinkedList<>();
	
	public List<Filter> getFilters() {
		return this.filters;
	}

	private List<Order> orders = new LinkedList<>();
	
	public List<Order> getOrders() {
		return this.orders;
	}
	
	/**
	 * Ensure that this is called, because it sets the parameter for reflection!
	 */
	public DAOTemplateImpl() {
		this.genType = ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		
		this.param = new ParameterizedListType(this.genType);
		
	}

	@SuppressWarnings("unchecked")
	public final Class<T> getPersistentClass() {
		return (Class<T>) genType;
	}

	public final Type getType() {
		return param;
	}
	
	static class ParameterizedListType implements ParameterizedType {
 
		ParameterizedListType(Type type) {
			this.type = type;
		}
		
		Type type;
		
		@Override
		public Type[] getActualTypeArguments() {
			return new Type[] { type };
		}
 
		@Override
		public Type getRawType() {
			return List.class;
		}
 
		@Override
		public Type getOwnerType() {
			return DAOTemplate.class;
		}
	}
}
