package fr.upem.ijavabook.server;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;

/**
 * Implentation how controls all of server flux.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ServerImpl implements Server {

    private final Vertx web_srv = Vertx.vertx();

    public ServerImpl() {
        System.setProperty("vertx.disableFileCaching", "true");//DEV
    }

    @Override
    public String start() {
        ArrayList<Route> routes = new ArrayList<>();
        routes.add(new Route("/exercise/:id", ServerImpl::getExerciceHandle));
        web_srv.deployVerticle(new RouteManager(routes));
        return "http://localhost:" + Servers.SERVER_PORT + "/";
    }

    public static void getExerciceHandle(RoutingContext rc) {
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