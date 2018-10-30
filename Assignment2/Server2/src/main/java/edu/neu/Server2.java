package edu.neu;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;


@Path("/")
public class Server2 {

    protected SharedDao sharedDao;

    @PostConstruct
    public void init() throws Exception {
        sharedDao = SharedDao.getInstance();
    }

    @PreDestroy
    public void destroy() {
        sharedDao = null;
    }

    @Path("hello")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        return "Hello from Jersey";
    }

    @Path("current/{userID}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String getMostRecent (@PathParam("userID") int userID) throws SQLException {
        int res = 0;
        try {
            res = sharedDao.getMostRecent(userID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return String.valueOf(res);
        }

    }

    @Path("single/{userID}/{day}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String getCountsByDay (@PathParam("userID") int userID, @PathParam("day") int day) throws SQLException {
        int res = 0;
        try {
            res = sharedDao.getCountsByDay(userID, day);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return String.valueOf(res);
        }
    }

    @Path("{userID}/{day}/{timeInterval}/{stepCount}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String postData(@PathParam("userID") int userID, @PathParam("day") int day,
                           @PathParam("timeInterval") int timeInterval, @PathParam("stepCount") int stepCount)
            throws Exception {
        String res = "";
        try {
            res = sharedDao.create(userID, day, timeInterval, stepCount);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return res;
        }
    }


}
