package com.neptune.api.template.adapter;

//TODO: change this when https://java.net/jira/browse/JAX_RS_SPEC-475 is fixed (follow: http://www.tagwith.com/question_1061564_jax-rs-hateoas-using-jersey-unwanted-link-properties-in-json)
// This is the better solution because you don't have to use a @Provider, which would increase the dependencies

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

public class LinkAdapter extends XmlAdapter<LinkAdapter.LinkJaxb, Link> {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LinkJaxb {

        @XmlAttribute(name = "href")
        private URI uri;

        @XmlAnyAttribute
        private Map<QName, Object> params;

        public LinkJaxb() {
            this(null, null);
        }

        public LinkJaxb(URI uri, Map<QName, Object> params) {
            this.uri = uri;
            this.params = params == null ? new HashMap<QName, Object>()
                    : params;
        }

        public URI getUri() {
            return uri;
        }

        public void setUri(URI uri) {
            this.uri = uri;
        }

        public Map<QName, Object> getParams() {
            return params;
        }

        public void setParams(Map<QName, Object> params) {
            this.params = params;
        }
    }

    @Override
    public LinkJaxb marshal(Link p1) {
        Map<QName, Object> params = new HashMap<>();
        for (Map.Entry<String, String> entry : p1.getParams().entrySet()) {
            params.put(new QName("", entry.getKey()), entry.getValue());
        }
        return new LinkJaxb(p1.getUri(), params);
    }

    @Override
    public Link unmarshal(LinkJaxb p1) {
        Link.Builder builder = Link.fromUri(p1.getUri());
        for (Map.Entry<QName, Object> entry : p1.getParams().entrySet()) {
            builder.param(entry.getKey().getLocalPart(),
                    entry.getValue().toString());
        }
        return builder.build();
    }
}