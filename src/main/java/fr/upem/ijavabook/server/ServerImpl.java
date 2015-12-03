package fr.upem.ijavabook.server;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Implentation how controls all of server flux.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ServerImpl implements Server {

    private final Vertx web_srv = Vertx.vertx();
    private final String directoryPath;

    ServerImpl(String directoryPath) {
        System.setProperty("vertx.disableFileCaching", "true");//DEV
        this.directoryPath = Objects.requireNonNull(directoryPath);
    }

    @Override
    public String start() {
        ArrayList<Route> routes = new ArrayList<>();
        routes.add(new Route(directoryPath, ServerImpl::getExerciceHandle));
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