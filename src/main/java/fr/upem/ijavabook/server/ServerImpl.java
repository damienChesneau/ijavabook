package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;
import fr.upem.ijavabook.jinterpret.InterpretedLine;
import fr.upem.ijavabook.jinterpret.JunitTestResult;
import fr.upem.ijavabook.server.manageclients.Client;
import fr.upem.ijavabook.server.manageclients.ClientsManager;
import fr.upem.ijavabook.server.transacparser.TransactionParser;
import fr.upem.ijavabook.server.transacparser.TransactionPattern;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation how controls all of server flux.
 *
 * @author Damien Chesneau
 */
class ServerImpl implements Server {

    private final Vertx webSrv = Vertx.vertx();
    private final ClientsManager clientsManager = new ClientsManager();
    private final Path rootDirectory;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * Create a new Server instance.
     * @param rootDirectory the repertory of this server.
     */
    ServerImpl(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        System.setProperty("vertx.disableFileCaching", "true");//DEV
    }

    @Override
    public String start() {
        RouteManager routeManager = initRouteManage();
        webSrv.deployVerticle(routeManager);
        return "http://localhost:" + Servers.SERVER_PORT + "/";
    }

    /**
     * This method initialize all routes with her action.
     * @return routeManager
     */
    private RouteManager initRouteManage() {
        ArrayList<Route> routes = new ArrayList<>();
        routes.add(new Route("/getallexercices/", this::getAllExerciseHandle, RequestType.GET));
        routes.add(new Route("/exercice/", this::getExercise, RequestType.POST));
        routes.add(new Route("/closeexercice/", this::closeExercise, RequestType.POST));
        routes.add(new Route("/javacode/", this::getJavaCode, RequestType.POST));
        routes.add(new Route("/junittest/", this::executeTest, RequestType.POST));
        return new RouteManager(routes, rootDirectory);
    }

    /**
     * This method are called when a client close his browser window.
     * So she manage the closing of opened instance like exercises and interpreter.
     * @param routingContext
     * @param exerciseService
     */
    private void closeExercise(RoutingContext routingContext, ExerciseService exerciseService) {
        routingContext.request().bodyHandler(event ->
                threadPool.execute(() -> {
                    TransactionParser<ArrayList<String>> vals = TransactionParser.parseAsObject(event.toString());
                    Path exerciseOfClient = Paths.get(vals.getMessage().get(1) + ".text");
                    exerciseService.closeExercise(exerciseOfClient);
                    int token = Integer.parseInt(vals.getMessage().get(0));
                    clientsManager.rmClient(token);
                }));
    }

    /**
     * Method called for first page. She respond with a list of exercises present.
     * @param routingContext
     * @param exerciseService
     */
    private void getAllExerciseHandle(RoutingContext routingContext, ExerciseService exerciseService) {
        threadPool.execute(() -> {
            try {
                List<String> filesNames = exerciseService.getFilesNamesWithoutExtension();
                routingResponse(routingContext, TransactionParser
                        .builderJavaList(TransactionPattern.RESPONSE_GET_ALL)
                        .setStringList(filesNames).build());
            } catch (IOException e) {
                routingResponseError(routingContext, e.getMessage());
            }
        });
    }

    /**
     * Method called when a new worker want to do an exercise.
     * This returns a token for identify client and the html of the client.
     * @param routingContext
     * @param exerciseService
     */
    private void getExercise(RoutingContext routingContext, ExerciseService exerciseService) {
        routingContext.request().bodyHandler(event ->
                threadPool.execute(() -> {
                    Path exerciseOfClient = Paths.get(event.toString() + ".text");
                    int token = clientsManager.newClient();
                    String exercise = exerciseService.playExercise(exerciseOfClient.normalize());
                    ArrayList<String> response = new ArrayList<>();
                    response.add(new TransactionParser<>(TransactionPattern.RESPONSE_NEW_TOKEN, token).toJson());
                    response.add(new TransactionParser<>(TransactionPattern.RESPONSE_EXERCISE, exercise).toJson());
                    routingResponse(routingContext, TransactionParser
                            .builderJavaList(TransactionPattern.RESPONSE_TOKEN_EXERCISE)
                            .setStringList(response)
                            .build());
                }));
    }

    /**
     * This method are called when a client want's to interpret a Java code.
     * This returns expression value and console output.
     * @param routingContext
     * @param exerciseService
     */
    private void getJavaCode(RoutingContext routingContext, ExerciseService exerciseService) {
        routingContext.request().bodyHandler(event ->
                threadPool.execute(() -> {
                    HashMap<TransactionPattern, String> requestParameters = parseJavaCodeRequest(event.toString());
                    Client clientByToken = getClient(requestParameters);
                    InterpretedLine interpreted = clientByToken.interpret(requestParameters.get(TransactionPattern.REQUEST_JAVA_CODE));
                    try {
                        List<String> consoleOutput = clientByToken.getOutput();
                        routingResponse(routingContext, TransactionParser
                                .builderJavaInterpreted(TransactionPattern.RESPONSE_CODE_OUTPUT, consoleOutput)
                                .setInterpretedLine(interpreted)
                                .build());
                    } catch (IOException e) {
                        routingResponseError(routingContext, "Can't get console output.");
                    }
                }));
    }

    /**
     * This method are used for test client java code.
     * @param routingContext
     * @param exerciseService
     */
    private void executeTest(RoutingContext routingContext, ExerciseService exerciseService) {
        routingContext.request().bodyHandler(event ->
                threadPool.execute(() -> {
                    HashMap<TransactionPattern, String> requestParameters = parseJavaCodeRequest(event.toString());
                    Client clientByToken = getClient(requestParameters);
                    JunitTestResult result = clientByToken.test(requestParameters.get(TransactionPattern.REQUEST_JUNIT_TEST));
                    routingResponse(routingContext, new TransactionParser<>(TransactionPattern.RESPONSE_JUNIT_RESULT, result.name()));
                }));
    }

    private void routingResponse(RoutingContext routingContext, TransactionParser<?> transactionParser) {
        routingContext.response()
                .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                .end(transactionParser.toJson());
    }

    private void routingResponseError(RoutingContext routingContext, String errorMessage) {
        routingContext.response()
                .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                .end((new TransactionParser<>(TransactionPattern.RESPONSE_ERROR, errorMessage)).toJson());
    }

    private Client getClient(HashMap<TransactionPattern, String> requestParameters) {
        String tokenAsString = requestParameters.get(TransactionPattern.REQUEST_TOKEN);
        return clientsManager.getClientByToken(Integer.parseInt(tokenAsString));
    }

    private HashMap<TransactionPattern, String> parseJavaCodeRequest(String toParse) {
        List<TransactionParser<String>> parse = TransactionParser.parseAsArray(toParse);
        HashMap<TransactionPattern, String> values = new HashMap<>();
        parse.forEach(line -> values.put(line.getType(), line.getMessage()));
        return values;
    }

    @Override
    public void stop() {
        threadPool.shutdown();
        webSrv.close();
    }

}