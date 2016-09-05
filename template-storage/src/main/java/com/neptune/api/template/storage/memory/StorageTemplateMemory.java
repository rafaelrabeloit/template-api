package com.neptune.api.template.storage.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.GenericEntity;

import com.neptune.api.template.dao.DAOTemplate;
import com.neptune.api.template.dao.DAOTemplateImpl;
import com.neptune.api.template.dao.Filtering;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.domain.DomainTemplate;

@MemoryStorage
public abstract class StorageTemplateMemory<T extends DomainTemplate>
        extends DAOTemplateImpl<T> implements DAOTemplate<T> {

    /**
     * Queue used for this elements
     */
    private static Map<Class<?>, Map<UUID, DomainTemplate>> DB = new HashMap<Class<?>, Map<UUID, DomainTemplate>>();

    @Inject
    public StorageTemplateMemory() {
        super();
    }

    private Map<UUID, DomainTemplate> getTable() {
        Map<UUID, DomainTemplate> table = DB.get(getPersistentClass());
        if (table == null) {
            table = new HashMap<UUID, DomainTemplate>();
            DB.put(getPersistentClass(), table);
        }
        return table;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public GenericEntity<List<T>> page(Integer maxResults, Integer offset) {
        Map<UUID, DomainTemplate> table = getTable();

        // Transforms queue in List
        List<T> all = new ArrayList<>((Collection<T>) table.values());

        // check boundaries to the sublist
        int to = Math.min(offset + maxResults, all.size());

        List<T> selection = all.subList(offset, to);

        // generate a page from a sublist
        return new GenericEntity<List<T>>(selection, getType());
    }

    @Override
    public T create(T entity) {
        getTable().put(entity.getId(), entity);
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T retrieve(T entity) {
        T element = null;

        for (DomainTemplate i : getTable().values()) {
            if (i.equals(entity)) {
                element = (T) i;
                break;
            }
        }

        return element;
    }

    @Override
    public T update(T entity) {
        T original = this.retrieve(entity);
        if (original != null) {
            original.copy(entity);
            return entity;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T delete(T entity) {
        T e = (T) getTable().remove(entity.getId());
        if ( e != null) {
            return entity;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public List<Filtering> getFilters() {
        return null;
    }

    @Override
    public List<Ordering> getOrders() {
        return null;
    }

}
