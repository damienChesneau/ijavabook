package fr.upem.ijavabook.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Manage all routes.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class RouteManager extends AbstractVerticle {

    private final List<Route> routes;

    RouteManager(List<Route> routes) {
        this.routes = Collections.unmodifiableList(Objects.requireNonNull(routes));
    }

    /**
     * Vertx is a super instance.
     */
    @Override
    public void start()  {
        Router router = Router.router(vertx);
        // route to JSON REST APIs
        routes.forEach((routes) -> router.get(routes.getRoute()).handler(routes.getEvent()));
        // otherwise serve static pages
        router.route().handler(StaticHandler.create());
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::accept);
        httpServer.websocketHandler(RouteManager::webSocketExercise);
        httpServer.listen(Servers.SERVER_PORT);
    }

    static void webSocketExercise(ServerWebSocket sws) {
        if ("/exercice".equals(sws.path())) {
            sws.handler((buf) -> {
                System.out.println(buf);
                sws.writeFinalTextFrame("Hello");
            });
        }
    }

}
