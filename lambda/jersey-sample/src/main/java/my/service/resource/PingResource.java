package my.service.resource;


import java.util.Map;
import java.util.HashMap;

import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/server")
public class PingResource {

    /*
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.WILDCARD)
    public Response createPet() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");
        return Response.status(200).entity(pong).build();
    }
    */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String getStatus() {
        return ("alive");
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String postText(String content) {
       return (Integer.toString(content.length()));
    }
}
