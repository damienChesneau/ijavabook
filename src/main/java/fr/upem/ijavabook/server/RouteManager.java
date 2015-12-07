package fr.upem.ijavabook.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.nio.file.Path;
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
    private final Path rootDirectory;

    RouteManager(List<Route> routes, Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        this.routes = Collections.unmodifiableList(Objects.requireNonNull(routes));
    }

    /**
     * Vertx is a super instance.
     */
    @Override
    public void start() {
        Router router = Router.router(vertx);
        routes.forEach(routes -> {
            router.get(routes.getRoute()).handler(comingEvent -> {
                onlyCurrentComputer(comingEvent.request().getHeader("Host"));
                routes.getEvent().handle(comingEvent);
            });
        }); // route to JSON REST APIs
        router.route().handler(StaticHandler.create());// otherwise serve static pages
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::accept);
        httpServer.websocketHandler(this::webSocketExercise);
        httpServer.listen(Servers.SERVER_PORT);
    }

    private void webSocketExercise(ServerWebSocket serverWebSocket) {
        onlyCurrentComputer(serverWebSocket.headers().get("Host"));
        if ("/exercice".equals(serverWebSocket.path())) {
            ExerciseWebSockets ews = new ExerciseWebSockets(serverWebSocket, rootDirectory);
            serverWebSocket.handler(ews::start);
            serverWebSocket.closeHandler(ews::onClose);
        }
    }

    private void onlyCurrentComputer(String host) {
        if (!("localhost:" + Servers.SERVER_PORT).equals(host)) {
            throw new IllegalAccessError("Client are not allow to read this.");
        }
    }
}
