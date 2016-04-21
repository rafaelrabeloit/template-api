package com.neptune.templates.microservice.queue;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.GenericEntity;

import com.neptune.templates.microservice.dao.DAOTemplate;
import com.neptune.templates.microservice.dao.DAOTemplateImpl;
import com.neptune.templates.microservice.domain.DomainTemplate;

public abstract class QueueTemplateGeneric<Q extends DomainTemplate> extends
		DAOTemplateImpl<Q> implements DAOTemplate<Q> {

	public abstract DelayedQueue getQueue();
	
	/**
	 * Ensure that this is called, because it sets the parameter for reflection!
	 * 
	 * @throws IOException
	 */
	public QueueTemplateGeneric() throws IOException {
		super();
	}

	@Override
	public GenericEntity<List<Q>> page(Integer maxResults, Integer offset) {
		return null;
	}

	@Override
	public Q create(Q entity) {
		getQueue().add(new DelayedItem(Long.MAX_VALUE, entity.getResourceId(), entity));
		return entity;
	}

	@Override
	public Q retrieve(Q entity) {
		if (getQueue().peek().equals(new DelayedItem(entity.getResourceId()))) {
			return entity; 
		} else {
			return null;
		}
	}

	@Override
	public Q update(Q entity) {
		this.delete(entity);
		return this.create(entity);
	}

	@Override
	public Q delete(Q entity) {
		if (getQueue().remove(new DelayedItem(entity.getResourceId()))) {
			return entity; 
		} else {
			return null;
		}
	}

	@Override
	public Q modify(Q entity) {
		return this.modify(entity);
	}

}
