package com.neptune.api.template.storage.jpa;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * This map holds all managers for this request.
     */
    Map<String, EntityManager> managers = new HashMap<>();

    @Produces
    public EntityManager newEntityManager(InjectionPoint ip) {
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

        // If there is a manager to this request, so there is no need to
        // generate a new one
        if (managers.containsKey(name)) {
            return managers.get(name);
        } else {
            EntityManager manager = producer
                    .getEntityManagerFactory(unitName, name)
                    .createEntityManager();
            managers.put(name, manager);

            return manager;
        }
    }

    @PreDestroy
    public void dispose() {
        for (EntityManager manager : managers.values()) {
            if (manager.isOpen()) {
                manager.close();
            }
        }
    }
}
