package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;
import fr.upem.ijavabook.exmanager.Exercises;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implentation how controls all of server flux.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ServerImpl implements Server {

    private final Vertx web_srv = Vertx.vertx();
    private final ExerciseService exerciseManager;
    private final Path rootDirectory;

    ServerImpl(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        exerciseManager = Exercises.getExerciseSrv(rootDirectory);
        System.setProperty("vertx.disableFileCaching", "true");//DEV
    }

    @Override
    public String start() {
        ArrayList<Route> routes = new ArrayList<>();
        routes.add(new Route("/getallexercices/", this::getAllExerciseHandle));
        web_srv.deployVerticle(new RouteManager(routes, rootDirectory));
        return "http://localhost:" + Servers.SERVER_PORT + "/";
    }

    public void getAllExerciseHandle(RoutingContext routingContext) {
        List<Path> allByDirectory = exerciseManager.getAllByDirectory(rootDirectory);
        List<String> filesNames = allByDirectory.stream().map((file) -> {
            String filename = file.getFileName().toString();
            return filename.substring(0, filename.length() - 5);
        }).collect(Collectors.toList());
        routingContext.response()
                .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                .end(new TransactionParser.BuilderJavaList(TransactionPattern.RESPONSE_GET_ALL).setList(filesNames).build().toJson());
    }

    @Override
    public void stop() throws IllegalAccessException {
        exerciseManager.stopWatcher();
        web_srv.close();
    }

    @Override
    public ExerciseService getExerciceManager() {
        return this.exerciseManager;
    }
}