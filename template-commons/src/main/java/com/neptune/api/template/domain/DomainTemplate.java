package com.neptune.api.template.domain;

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
     * Serial Version UID
     */
    private static final long serialVersionUID = -8164385543051858159L;

    private UUID mId;
    private Date mCreatedOn;

    /**
     * Basic Constructor
     */
    public DomainTemplate() {
        this.setId(UUID.randomUUID());
        this.setCreatedOn(new Date());
    }

    @Id
    @Column(columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    public UUID getId() {
        return this.mId;
    }

    public void setId(UUID id) {
        this.mId = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", nullable = false, updatable = false)
    public Date getCreatedOn() {
        return (Date) mCreatedOn.clone();
    }

    public void setCreatedOn(Date createdOn) {
        this.mCreatedOn = (Date) createdOn.clone();
    }

    @PrePersist
    protected void onCreate() {
        this.mCreatedOn = new Date();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        if (getId() != null) {
            result += getId().hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DomainTemplate other = (DomainTemplate) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }
}
