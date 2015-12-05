package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;
import fr.upem.ijavabook.exmanager.Exercises;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.*;

/**
 * Manage all routes.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class RouteManager extends AbstractVerticle {

    private final List<Route> routes;
    private final ExerciseService exerciceManager = Exercises.getExerciseSrv();
    private final Thread watcher = exerciceManager.start();

    RouteManager(List<Route> routes) {
        this.routes = Collections.unmodifiableList(Objects.requireNonNull(routes));
    }

    /**
     * Vertx is a super instance.
     */
    @Override
    public void start() {
        Router router = Router.router(vertx);
        routes.forEach((routes) -> router.get(routes.getRoute()).handler(routes.getEvent())); // route to JSON REST APIs
        router.route().handler(StaticHandler.create());// otherwise serve static pages
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::accept);
        httpServer.websocketHandler(RouteManager::webSocketExercise);
        httpServer.listen(Servers.SERVER_PORT);
    }

    public String getExercise(String name, Observer observer) {
        return exerciceManager.getExercise(name,observer);
    }

    private static void webSocketExercise(ServerWebSocket sws) {
        if ("/exercice".equals(sws.path())) {
            ExerciseWebSockets ews = new ExerciseWebSockets(sws);
            Thread client = new Thread(new EncaplsulateWebSock(ews, sws));
            client.start();
        }
    }

    private static class EncaplsulateWebSock implements Runnable,Observer {
        private final ExerciseWebSockets ews;
        private final ServerWebSocket sws;

        public EncaplsulateWebSock(ExerciseWebSockets ews, ServerWebSocket sws) {
            this.ews = Objects.requireNonNull(ews);
            this.sws = Objects.requireNonNull(sws);
        }

        @Override
        public void run() {
            sws.handler(ews::start);
            sws.closeHandler(ews::onClose);
        }

        @Override
        public void update(Observable o, Object arg) {
            ews.updateWebSock((String)arg);
        }
    }

}
