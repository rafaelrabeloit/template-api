package com.neptune.templates.microservice.adapter;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, Link>> {
 
    public static class AdaptedMap {
         
        @XmlVariableNode("key")
        List<AdaptedEntry> entries = new ArrayList<AdaptedEntry>();
         
    }

    public static class AdaptedEntry {
         
        @XmlTransient
        public String key;
         
        // XMLValue expects a Java Bean
        @XmlJavaTypeAdapter(LinkAdapter.class)
        @XmlPath(".")
        public Link value;
 
    }
 
    @Override
    public AdaptedMap marshal(Map<String, Link> map) {
    	
        AdaptedMap adaptedMap = new AdaptedMap();
        for(Entry<String, Link> entry : map.entrySet()) {
            AdaptedEntry adaptedEntry = new AdaptedEntry();
            adaptedEntry.key = entry.getKey();
            adaptedEntry.value = entry.getValue();
            adaptedMap.entries.add(adaptedEntry);
        }
        return adaptedMap;
    }
 
    @Override
    public Map<String, Link> unmarshal(AdaptedMap adaptedMap) {
    	
        List<AdaptedEntry> adaptedEntries = adaptedMap.entries;
        Map<String, Link> map = new HashMap<String, Link>(adaptedEntries.size());
        for(AdaptedEntry adaptedEntry : adaptedEntries) {
            map.put(adaptedEntry.key, adaptedEntry.value);
        }
        return map;
    }
 
}