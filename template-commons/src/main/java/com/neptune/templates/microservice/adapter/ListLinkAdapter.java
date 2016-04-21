package com.neptune.templates.microservice.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ListLinkAdapter extends XmlAdapter<ListLinkAdapter.ListLinkJaxb, List<Link>> {  

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ListLinkJaxb {  
	
	    @XmlPath(".")
	    @XmlJavaTypeAdapter(MapAdapter.class)
	    public Map<String, Link> links;
	
	    public ListLinkJaxb() { }
	    
		public Map<String, Link> getLinks() {
			return links;
		}
	
		public void setLinks(Map<String, Link> links) {
			this.links = links;
		}  
	}

	@Override
    public ListLinkJaxb marshal(List<Link> list) {
    	ListLinkJaxb ret = new ListLinkJaxb();    	
        Map<String, Link> links = new HashMap<>();
        
        for(Link link: list) {
        	links.put(link.getRel(), link);  
        }
        
        ret.setLinks(links);
    	return ret;
    }  

	@Override
    public List<Link> unmarshal(ListLinkJaxb list) {  
    	throw new UnsupportedOperationException();  
    }  
}  
