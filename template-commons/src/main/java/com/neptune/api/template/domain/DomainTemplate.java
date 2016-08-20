package com.neptune.api.template.domain;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

/**
 * Domain Template class that MUST be implemented to ensure type, and to be used
 * in generics :)
 * 
 * @author Rafael Rabelo
 *
 */
public abstract class DomainTemplate {

    /**
     * 
     */
    public DomainTemplate() {

        this.setCreatedOn(new Date());
    }

    /**
     * Every model MUST have an Id
     * @return Integer Id
     */
    public abstract UUID getId();
    
    /**
     * Because this id is probably a ORM property, it also must has a setter
     * @param id
     * @return
     */
    public abstract void setId(UUID id);
;
    public abstract Date getCreatedOn();

    public abstract void setCreatedOn(Date createdOn);
    
    protected abstract void onCreate();
    
    /**
     * Copy from another object's properties
     * @param t target object to copy from
     * @throws IllegalArgumentException
     */
    public void copy(Object t) throws IllegalArgumentException {
        if (this.getClass() == t.getClass()) {
            for (Field field : t.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    field.set(this, field.get(t));
                } catch (IllegalAccessException e) {
                }
            }
        }
    }
}
