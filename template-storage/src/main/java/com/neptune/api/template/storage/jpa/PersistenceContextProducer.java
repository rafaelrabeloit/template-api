package com.neptune.api.template.storage.jpa;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class PersistenceContextProducer {

    @Inject
    PersistenceUnitProducer producer;

    EntityManager manager = null;

    @Produces
    @PersistenceContext
    public EntityManager newEntityManager(InjectionPoint ip) {
        // If there is a manager to this request, so there is no need to
        // generate a new one
        if (manager != null) {
            return manager;
        }

        PersistenceContext ann = null;

        if (ip != null) {
            ann = ip.getAnnotated().getAnnotation(PersistenceContext.class);
        }

        String unitName = null;
        String name = null;

        if (ann != null) {
            name = ann.name();
            unitName = ann.unitName();
        }

        if (unitName == null) {
            unitName = "";
        }

        if (name == null) {
            name = "";
        }

        return producer.getEntityManagerFactory(unitName, name)
                .createEntityManager();
    }

    @PreDestroy
    public void dispose() {
        if (manager != null && manager.isOpen()) {
            manager.close();
        }
    }
}
