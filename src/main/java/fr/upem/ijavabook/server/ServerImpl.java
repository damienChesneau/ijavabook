package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;
import fr.upem.ijavabook.exmanager.Exercises;
import fr.upem.ijavabook.jinterpret.InterpretedLine;
import fr.upem.ijavabook.server.manageclients.Client;
import fr.upem.ijavabook.server.manageclients.ClientsManager;
import fr.upem.ijavabook.server.transacparser.TransactionParser;
import fr.upem.ijavabook.server.transacparser.TransactionPattern;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implentation how controls all of server flux.
 *
 * @author Damien Chesneau
 */
class ServerImpl implements Server {

    private final Vertx web_srv = Vertx.vertx();
    private final ClientsManager clientsManager = new ClientsManager();
    private final Path rootDirectory;
    private final RouteManager routeManager;

    ServerImpl(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        System.setProperty("vertx.disableFileCaching", "true");//DEV
        routeManager = initRouteManage();
    }

    private RouteManager initRouteManage() {
        ArrayList<Route> routes = new ArrayList<>();
        routes.add(new Route("/getallexercices/", this::getAllExerciseHandle, RequestType.GET));
        routes.add(new Route("/exercice/", this::getExercise, RequestType.POST));
        routes.add(new Route("/javacode/", this::getJavaCode, RequestType.POST));
        return new RouteManager(routes, rootDirectory);
    }

    @Override
    public String start() {
        web_srv.deployVerticle(routeManager);
        return "http://localhost:" + Servers.SERVER_PORT + "/";
    }

    public void getAllExerciseHandle(RoutingContext routingContext, ExerciseService exerciseService) {
        List<Path> allByDirectory = exerciseService.getAllByDirectory(rootDirectory);
        List<String> filesNames = allByDirectory.stream().map((file) -> {
            String filename = file.getFileName().toString();
            return filename.substring(0, filename.length() - 5);
        }).collect(Collectors.toList());
        routingContext.response()
                .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                .end(TransactionParser.builderJavaList(TransactionPattern.RESPONSE_GET_ALL).setList(filesNames).build().toJson());
    }

    private void getExercise(RoutingContext routingContext, ExerciseService exerciseService) {
        int token = clientsManager.newClient();
        routingContext.request().bodyHandler(event -> {
            String exercise = exerciseService.getExercise(rootDirectory.resolve(event.toString() + ".text").normalize(), null/* clientsManager.getObserver(token)*/);
            ArrayList<String> response = new ArrayList<>();
            response.add(new TransactionParser(TransactionPattern.RESPONSE_NEW_TOKEN, token).toJson());
            response.add(new TransactionParser(TransactionPattern.RESPONSE_EXERCISE, exercise).toJson());
            routingContext.response()
                    .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                    .end(TransactionParser.builderJavaList(TransactionPattern.RESPONSE_TOKEN_EXERCISE).setList(response).build().toJson());
        });
    }

    private void getJavaCode(RoutingContext routingContext, ExerciseService exerciseService) {
        routingContext.request().bodyHandler(event -> {
            HashMap<TransactionPattern, String> requestParameters = parseJavaCodeRequest(event.toString());
            String tokenAsString = requestParameters.get(TransactionPattern.REQUEST_TOKEN);
            Client clientByToken = clientsManager.getClientByToken(Integer.parseInt(tokenAsString));
            InterpretedLine interpreted = clientByToken.interpret(requestParameters.get(TransactionPattern.REQUEST_JAVA_CODE));
            routingContext.response()
                    .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                    .end(TransactionParser.builderJavaInterpreted(TransactionPattern.RESPONSE_CODE_OUTPUT, clientByToken.getOutput())
                            .setInterpretedLine(interpreted).build().toJson());
        });
    }

    private HashMap<TransactionPattern, String> parseJavaCodeRequest(String toParse) {
        List<TransactionParser<String>> parse = TransactionParser.parseAsArray(toParse);
        HashMap<TransactionPattern, String> values = new HashMap<>();
        parse.forEach(line -> values.put(line.getType(), line.getMessage()));
        return values;
    }

    @Override
    public void stop() {
        web_srv.close();
    }

}