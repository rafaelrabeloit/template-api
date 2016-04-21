package com.neptune.templates.microservice.dao;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.persistence.Column;
import javax.ws.rs.core.GenericEntity;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.criterion.Restrictions;

import com.neptune.templates.microservice.dao.util.HibernateUtil;
import com.neptune.templates.microservice.domain.DomainTemplate;

/**
 * 
 * @author Rafael Rabelo
 *
 * @param <T>
 *            The Domain class that this DAO will manage
 */
@HibernateDAO
public abstract class DAOTemplateHibernate<T extends DomainTemplate> extends
		DAOTemplateImpl<T> implements DAOTemplate<T> {

	/**
	 * Ensure that this is called, because it sets the parameter for reflection!
	 */
	public DAOTemplateHibernate() {
		super();
	}

	/**
	 * This can be used safely TODO: limit maxResults!
	 * 
	 * @param maxResults
	 * @param offset
	 * @return a paginated list of items
	 */
	@SuppressWarnings("unchecked")
	@Override
	public GenericEntity<List<T>> page(Integer maxResults, Integer offset) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(getPersistentClass())
				.setFirstResult(offset).setMaxResults(maxResults);

		this.composeCriteria(criteria);
		
		GenericEntity<List<T>> result = new GenericEntity<List<T>>(
				criteria.list(), getType());

		session.close();

		return result;
	}

	/**
	 * Read in CRUD Must be exposed to identify resources
	 * 
	 * @param resourceId
	 * @return entity that has the specified resourceId
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T retrieve(T entity) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(getPersistentClass()).add(
				Restrictions.eq("id", entity.getId()));

		entity = (T) criteria.uniqueResult();

		session.close();

		return entity;
	}

	/**
	 * Create in CRUD
	 */
	@Override
	public T create(T entity) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			session.save(entity);

			tx.commit();
		} catch (RuntimeException e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}

		return entity;
	}

	/**
	 * Delete in CRUD
	 */
	@Override
	public T delete(T entity) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			entity = this.retrieve(entity);

			session.delete(entity);

			tx.commit();
		} catch (RuntimeException e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}

		return entity;
	}

	/**
	 * Update in CRUD
	 */
	@Override
	public T update(T entity) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			session.update(entity);

			tx.commit();
		} catch (RuntimeException e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}

		return entity;
	}

	@Override
	public T modify(T entity) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Integer id = entity.getId();

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			StringBuilder hql = new StringBuilder("UPDATE ");
			hql.append(this.getPersistentClass().getSimpleName());
			hql.append(" u");
			hql.append(" SET ");

			Map<String, Object> validProperties = this
					.mapValidProperties(entity);
			int count = 0;
			for (String propertyName : validProperties.keySet()) {
				hql.append(propertyName).append("=").append(":")
						.append(propertyName);
				if (count < validProperties.keySet().size() - 1) {
					hql.append(", ");
				}
				count++;
			}

			hql.append(" WHERE ").append("u.id = :id");

			Query query = session.createQuery(hql.toString()).setInteger("id",
					id);

			for (String propertyName : validProperties.keySet()) {
				query.setParameter(propertyName,
						validProperties.get(propertyName));
			}

			int res = query.executeUpdate();

			if (res == 0)
				throw new NoSuchElementException();
			else if (res > 1) {
				throw new UnresolvableObjectException(entity.getId(),
						getPersistentClass().getSimpleName());
			}

			tx.commit();

			entity = this.retrieve(entity);

		} catch (Exception e) {
			tx.rollback();
		} finally {
			session.close();
		}

		return entity;
	}

	/**
	 * Update only Fields that are not null int the instance.
	 * 
	 * @throws Exception
	 */
	public Map<String, Object> mapValidProperties(T entity) throws Exception {
		HashMap<String, Object> result = new HashMap<String, Object>();

		try {
			Column ann;

			for (PropertyDescriptor propertyDescriptor : Introspector
					.getBeanInfo(this.getPersistentClass(), Object.class)
					.getPropertyDescriptors()) {

				Object val = propertyDescriptor.getReadMethod().invoke(entity);
				ann = (Column) propertyDescriptor.getReadMethod()
						.getAnnotation(Column.class);
				if (ann != null && (val != null || ann.nullable())
						&& ann.updatable()) {
					result.put(
							ann.name().equals("") ? propertyDescriptor
									.getName() : ann.name(), val);
				}
			}

		} catch (RuntimeException | IllegalAccessException
				| InvocationTargetException e) {
			throw e;
		}

		return result;
	}

	
	public void composeCriteria(Criteria criteria) {

		for (Filter f : this.getFilters()) {
			if (f.getOperation() == Filter.Operation.EQUAL)
				criteria.add(Restrictions.eq(f.getProperty(), f.getValue()));
		}

		for (Order o : this.getOrders()) {
			if (o.getDirection() == Order.Direction.DESC)
				criteria.addOrder(org.hibernate.criterion.Order.desc(o.getField()));
		}
		
	}
}
