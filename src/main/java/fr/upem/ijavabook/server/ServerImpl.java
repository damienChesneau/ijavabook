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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Implentation how controls all of server flux.
 *
 * @author Damien Chesneau
 */
class ServerImpl implements Server {

    private final Vertx webSrv = Vertx.vertx();
    private final ClientsManager clientsManager = new ClientsManager();
    private final Path rootDirectory;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(20);

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

    private RouteManager initRouteManage() {
        ArrayList<Route> routes = new ArrayList<>();
        routes.add(new Route("/getallexercices/", (r,e)-> executeInThread(r,e,this::getAllExerciseHandle), RequestType.GET));
        routes.add(new Route("/exercice/", (r,e)-> executeInThread(r,e,this::getExercise), RequestType.POST));
        routes.add(new Route("/closeexercice/", (r,e)-> executeInThread(r,e,this::closeExercise), RequestType.POST));
        routes.add(new Route("/javacode/", (r,e)-> executeInThread(r,e,this::getJavaCode), RequestType.POST));
        routes.add(new Route("/junittest/",(r,e)-> executeInThread(r,e,this::executeTest),RequestType.POST));
        return new RouteManager(routes, rootDirectory);
    }

    private void executeInThread(RoutingContext routingContext, ExerciseService exerciseService, Route.EventWithExercise eventWithExercise){
        /*threadPool.execute(()->*/eventWithExercise.doAction(routingContext,exerciseService)/*)*/;
    }

    private void closeExercise(RoutingContext routingContext, ExerciseService exerciseService) {
        /*threadPool.execute(()-> */routingContext.request().bodyHandler(event -> {
            TransactionParser<ArrayList<String>> vals = TransactionParser.parseAsObject(event.toString());
            Path exerciseOfClient = Paths.get(vals.getMessage().get(1) + ".text");
            exerciseService.closeExercise(exerciseOfClient);
            int token = Integer.parseInt(vals.getMessage().get(0));
            clientsManager.rmClient(token);
        })/*)*/;
    }

    public void getAllExerciseHandle(RoutingContext routingContext, ExerciseService exerciseService) {
            List<Path> allByDirectory = exerciseService.getAllByDirectory();
            List<String> filesNames = allByDirectory.stream().map((file) -> {
                String filename = file.getFileName().toString();
                return filename.substring(0, filename.length() - 5);
            }).collect(Collectors.toList());
            routingResponse(routingContext, TransactionParser
                    .builderJavaList(TransactionPattern.RESPONSE_GET_ALL)
                    .setList(filesNames).build());
        //});
    }

    private void getExercise(RoutingContext routingContext, ExerciseService exerciseService) {
        /*threadPool.execute(()-> */routingContext.request().bodyHandler(event -> {
            Path exerciseOfClient = Paths.get(event.toString() + ".text");
            int token = clientsManager.newClient(exerciseOfClient);
            String exercise = exerciseService.playExercise(exerciseOfClient.normalize());
            ArrayList<String> response = new ArrayList<>();
            response.add(new TransactionParser(TransactionPattern.RESPONSE_NEW_TOKEN, token).toJson());
            response.add(new TransactionParser(TransactionPattern.RESPONSE_EXERCISE, exercise).toJson());
            routingResponse(routingContext,TransactionParser
                    .builderJavaList(TransactionPattern.RESPONSE_TOKEN_EXERCISE)
                    .setList(response)
                    .build());
        })/*)*/;
    }

    private void getJavaCode(RoutingContext routingContext, ExerciseService exerciseService) {
        /*threadPool.execute(()->*/ routingContext.request().bodyHandler(event -> {
            HashMap<TransactionPattern, String> requestParameters = parseJavaCodeRequest(event.toString());
            Client clientByToken = getClient(requestParameters);
            InterpretedLine interpreted = clientByToken.interpret(requestParameters.get(TransactionPattern.REQUEST_JAVA_CODE));
            routingResponse(routingContext,TransactionParser
                    .builderJavaInterpreted(TransactionPattern.RESPONSE_CODE_OUTPUT, clientByToken.getOutput())
                    .setInterpretedLine(interpreted)
                    .build());
        })/*)*/;
    }

    private void executeTest(RoutingContext routingContext, ExerciseService exerciseService) {
        /*threadPool.execute(()->*/ routingContext.request().bodyHandler(event -> {
            HashMap<TransactionPattern, String> requestParameters = parseJavaCodeRequest(event.toString());
            Client clientByToken = getClient(requestParameters);
            JunitTestResult result = clientByToken.test(requestParameters.get(TransactionPattern.REQUEST_JUNIT_TEST));
            routingResponse(routingContext,new TransactionParser(TransactionPattern.RESPONSE_JUNIT_RESULT, result.name()));
        })/*)*/;
    }

    private void routingResponse(RoutingContext routingContext,TransactionParser transactionParser){
        routingContext.response()
                .putHeader(ContentTypeVal.KEY_VALUE.getContent(), ContentTypeVal.APPLICATION_JSON.getContent())
                .end(transactionParser.toJson());
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