package edu.neu;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/server")
public class Server {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
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