package com.neptune.api.template.storage.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

@Singleton
public class PersistenceUnitProducer {

    /**
     * This map holds all persistence units, if there is more than one.
     */
    static Map<String, EntityManagerFactory> units = new HashMap<>();

    @Produces
    @PersistenceUnit(unitName = "")
    public EntityManagerFactory createFactory(InjectionPoint ip) {
        EntityManagerFactory emf;

        PersistenceUnit ann = null;

        if (ip != null) {
            ann = ip.getAnnotated().getAnnotation(PersistenceUnit.class);
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

        emf = getEntityManagerFactory(unitName, name);

        return emf;
    }

    EntityManagerFactory getEntityManagerFactory(String unitName, String name) {
        EntityManagerFactory emf;
        if (units.containsKey(name)) {
            emf = units.get(name);
        } else {
            emf = Persistence.createEntityManagerFactory(unitName);
            units.put(name, emf);
        }
        return emf;
    }

    @PreDestroy
    public void dispose() {
        for(EntityManagerFactory e: units.values()) {
            e.close();
        }
    }
}
