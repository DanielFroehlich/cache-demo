package com.redhat.sie;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/cache")
public class CacheResource {

	@Inject
	CacheService cacheService;

    @GET
    @Path("/build")
    // @Produces(MediaType.APPLICATION_JSON)
    public Response createCache(@QueryParam(value = "type") String type, @QueryParam(value = "name") String name) {
    	return Response.ok(cacheService.createCache(name, type)).build();
    }

    @GET
    @Path("/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeCache(@QueryParam(value = "name") String name) {
    	return Response.ok(cacheService.removeCache(name)).build();
    }

    @GET
    @Path("/fill")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fillCache(@QueryParam(value = "entries") int entries) {
        return Response.ok(cacheService.fillCache(entries)).build();
    }

    @GET
    @Path("/dump")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dumpCache() {
        return Response.ok(cacheService.dumpCache()).build();
    }

		@GET
    @Path("/daniel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response daniel() {
        return Response.ok(cacheService.daniel()).build();
    }


}
