package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;
import fr.upem.ijavabook.exmanager.Exercises;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Observer;

/**
 * Implentation how controls all of server flux.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ServerImpl implements Server {

    private final Vertx web_srv = Vertx.vertx();

    ServerImpl() {
        System.setProperty("vertx.disableFileCaching", "true");//DEV
    }

    @Override
    public String start() {
        ArrayList<Route> routes = new ArrayList<>();
        routes.add(new Route("/exercise/:id", ServerImpl::getExerciceHandle));
        web_srv.deployVerticle(new RouteManager(routes));
        return "http://localhost:" + Servers.SERVER_PORT + "/";
    }


    private static void getExerciceHandle(RoutingContext rc) {
        String id = rc.request().getParam("id");
        System.out.println(id);
        rc.response()
                .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                .end("{val:5}");
    }

    @Override
    public void stop() {
        web_srv.close();
    }
}