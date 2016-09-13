package com.neptune.api.template.adapter;

//TODO: change this when https://java.net/jira/browse/JAX_RS_SPEC-475 is fixed (follow: http://www.tagwith.com/question_1061564_jax-rs-hateoas-using-jersey-unwanted-link-properties-in-json)
// This is the better solution because you don't have to use a @Provider,
// which would increase the dependencies

import java.net.URI;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class LinkAdapter extends XmlAdapter<LinkJaxb, Link> {

    public LinkAdapter() {
    }

    public Link unmarshal(LinkJaxb p1) {
        throw new UnsupportedOperationException();
    }

    public LinkJaxb marshal(Link p1) {
        return new LinkJaxb(p1.getUri(), p1.getRel());
    }
}

final class LinkJaxb {

    private URI mUri;
    private String mRel;

    public LinkJaxb() {
        this(null, null);
    }

    public LinkJaxb(URI uri) {
        this(uri, null);
    }

    public LinkJaxb(URI uri, String rel) {
        this.mUri = uri;
        this.mRel = rel;
    }

    @XmlAttribute(name = "href")
    public URI getUri() {
        return mUri;
    }

    @XmlAttribute(name = "rel")
    public String getRel() {
        return mRel;
    }

    public void setUri(URI uri) {
        this.mUri = uri;
    }

}