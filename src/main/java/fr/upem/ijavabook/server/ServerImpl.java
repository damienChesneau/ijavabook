package fr.upem.ijavabook.server;

import io.vertx.core.Vertx;

import java.util.ArrayList;

/**
 * Implentation how contols all of server flux.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ServerImpl implements Server {

    private final Vertx WEB_SRV = Vertx.vertx();

    public ServerImpl(){
        System.setProperty("vertx.disableFileCaching", "true");//DEV
    }

    @Override
    public void start() {
        ArrayList<Route> routes = new ArrayList<>();
        routes.add(new Route("/exercise/:id", (rc)->{
            String id = rc.request().getParam("id");
            rc.response()
                    .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                    .end(/*exercise.toJSON()*/);
        }));
        WEB_SRV.deployVerticle(new RouteManager(routes));
    }

    @Override
    public void stop() {
        WEB_SRV.close();
    }
}