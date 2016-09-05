package com.neptune.api.template.domain;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Domain Template class that MUST be implemented to ensure type, and to be used
 * in generics :)
 * 
 * @author Rafael Rabelo
 *
 */
@MappedSuperclass
public abstract class DomainTemplate implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8164385543051858159L;

    private UUID id;
    private Date createdOn;

    /**
     * 
     */
    public DomainTemplate() {
        this.setId(UUID.randomUUID());
        this.setCreatedOn(new Date());
    }

    @Id
    @Column(columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", nullable = false, updatable = false)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @PrePersist
    protected void onCreate() {
        this.createdOn = new Date();
    }

    /**
     * Copy from another object's properties
     * 
     * @param t
     *            target object to copy from
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DomainTemplate other = (DomainTemplate) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }
}
