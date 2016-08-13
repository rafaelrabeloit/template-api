package com.neptune.api.template.resource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.ValidationException;

import com.neptune.api.template.domain.DomainTemplate;
import com.neptune.api.template.service.ServiceTemplate;

//TODO: Validations everywhere!
/**
 * 
 * @author Rafael Rabelo
 *
 * @param <E>
 *            The Domain class that represents this Resource
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
abstract public class ResourceTemplate<E extends DomainTemplate> {

    private Type genType;

    public ResourceTemplate() {
        this.genType = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    public Class<E> getDomainClass() {
        return (Class<E>) this.genType;
    }

    /**
     * This function will expose the service to the parent, more generic. If any
     * different implementation to the functions below is required, they can be
     * override.
     * 
     * @return The Service that will be used to the operations on the database
     */
    public abstract ServiceTemplate<E> getService();

    @GET
    public Response index(@DefaultValue("1") @QueryParam("page") int pageNumber,
            @DefaultValue("10") @QueryParam("per_page") int pageQty) {
        // TODO: search queries
        // TODO: change everything from GenericEntity to a Result class, because
        // of result metadata

        GenericEntity<List<E>> list = this.getService().page(pageQty,
                pageQty * (pageNumber - 1));
        return Response.status(Status.OK).entity(list).build();
    }

    @POST
    public Response add(E entity) {
        this.getService().create(entity);

        return Response.status(Status.CREATED).entity(entity).build();
    }

    @GET
    @Path("/{id : \\d+}")
    public Response get(@PathParam("id") String id) {

        E entity = null;

        try {
            entity = this.getDomainClass().newInstance();
            entity.setResourceId(id);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } catch (ValidationException e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }

        entity = this.getService().retrieve(entity);

        return Response.status(Status.OK).entity(entity).build();
    }

    @PUT
    @Path("/{id : \\d+}")
    public Response put(@PathParam("id") String id, E entity) {

        try {
            // TODO: perform id check
            entity.setResourceId(id);
            entity = this.getService().update(entity);

            return Response.status(Status.OK).entity(entity).build();
        } catch (ValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }

    }

    @DELETE
    @Path("/{id : \\d+}")
    public Response delete(@PathParam("id") String id) {
        E entity = null;

        try {
            entity = this.getDomainClass().newInstance();
            entity.setResourceId(id);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } catch (ValidationException e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }

        entity = this.getService().delete(entity);

        return Response.status(Status.OK).entity(entity).build();
    }

}
