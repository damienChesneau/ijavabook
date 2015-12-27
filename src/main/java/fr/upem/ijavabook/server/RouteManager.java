package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;
import fr.upem.ijavabook.exmanager.Exercises;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Manage all routes.
 *
 * @author Damien Chesneau
 */
class RouteManager extends AbstractVerticle {

    private final List<Route> routes;
    private final Path rootDirectory;

    /**
     * Create a RouteManager
     *
     * @param routes        Each routes who will be added in the Verticle
     * @param rootDirectory The repertory of the server instance
     */
    RouteManager(List<Route> routes, Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        this.routes = Collections.unmodifiableList(Objects.requireNonNull(routes));
    }

    /**
     * Vertx is a super instance.
     */
    @Override
    public void start() throws IOException {
        Router router = Router.router(vertx);
        EventBus eb = vertx.eventBus();
        ExerciseService exerciseSrv = Exercises.getExerciseSrv(rootDirectory, new EventBusSenderImpl(eb));

        routes.forEach(routes -> routes.getRequestType().getRequestTypeApplication(router, routes.getRoute()).handler(comingEvent -> {
            onlyCurrentComputer(comingEvent.request().getHeader("Host"));
            routes.getEvent().doAction(comingEvent, exerciseSrv);
        })); // route to JSON REST APIs
        BridgeOptions opts = getBridgeOptions(exerciseSrv);
        SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
        router.route("/eventbus/*").handler(ebHandler);
        router.route().handler(StaticHandler.create());// otherwise serve static pages
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::accept);
        httpServer.listen(Servers.SERVER_PORT);
    }

    private BridgeOptions getBridgeOptions(ExerciseService exerciseService) throws IOException {
        BridgeOptions bridgeOptions = new BridgeOptions();
        List<String> filesNamesWithoutExtension = exerciseService.getFilesNamesWithoutExtension();
        ArrayList<PermittedOptions> po = new ArrayList<>();
        filesNamesWithoutExtension.forEach(s -> po.add(new PermittedOptions().setAddress(s)));
        bridgeOptions.setOutboundPermitted(po);
        return bridgeOptions;
    }

    private void onlyCurrentComputer(String host) {
        if (!("localhost:" + Servers.SERVER_PORT).equals(host)) {
            throw new IllegalAccessError("Client are not allow to read this.");
        }
    }
}
