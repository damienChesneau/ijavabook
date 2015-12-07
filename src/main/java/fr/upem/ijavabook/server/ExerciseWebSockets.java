package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;
import fr.upem.ijavabook.exmanager.Exercises;
import fr.upem.ijavabook.jinterpret.InterpretedLine;
import fr.upem.ijavabook.jinterpret.Interpreter;
import fr.upem.ijavabook.jinterpret.Interpreters;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Define all request/responses to client and server for exercises socket.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ExerciseWebSockets implements Observer {
    /**
     * Define all methods.
     */
    private final HashMap<TransactionPattern, Function<TransactionParser, String>> operations = new HashMap<>();
    private final ServerWebSocket sws;
    private final Interpreter interpreter = Interpreters.getJavaInterpreter();
    private final ExerciseService exerciseManager;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final Path rootDirectory;

    /**
     * @param sws ServerWebSocket instance to write and recives datas.
     */
    ExerciseWebSockets(ServerWebSocket sws, Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        this.sws = Objects.requireNonNull(sws);
        this.operations.put(TransactionPattern.REQUEST_ASK_EXERCISE, this::requerstAnExercice);
        this.operations.put(TransactionPattern.REQUEST_JAVA_CODE, this::requerstAnJavaCode);
        this.exerciseManager = Servers.getExerciceManager();
    }

    /**
     * Start method to use when a new connection comes.
     *
     * @param buf
     */
    public void start(Buffer buf) {
        threadPool.execute(() -> {
            TransactionParser tp = TransactionParser.parse(String.valueOf(buf));
            sws.writeFinalTextFrame(operations.get(tp.getType()).apply(tp));
        });
    }

    final String requerstAnExercice(TransactionParser<String> tp) {
        String exercise = getExercise(getExercisePath(Paths.get(tp.getMessage() + ".text")));
        TransactionParser creator = new TransactionParser(TransactionPattern.RESPONSE_EXERCISE, exercise);
        return creator.toJson();
    }

    private final String requerstAnJavaCode(TransactionParser<String> tp) {
        List<InterpretedLine> interpret = interpreter.interpretAll(Arrays.asList(tp.getMessage().split("\\n")));
        String reduce = interpreter.getOutput().stream().collect(Collectors.joining("<br/>"));
        TransactionParser c = new TransactionParser.
                BuilderJavaInterpreted(TransactionPattern.RESPONSE_CODE_OUTPUT, reduce)
                .setInterpretedLines(interpret).build();
        return c.toJson();
    }

    /**
     * To use for close interpreter instance.
     *
     * @param voiD instance
     * @return NPE
     */
    public ServerWebSocket onClose(Void voiD) {
        interpreter.close();
        threadPool.shutdown();
        exerciseManager.removeObserver(this);
        return sws;
    }

    private Path getExercisePath(Path exercise) {
        return rootDirectory.resolve(exercise).normalize();
    }

    private String getExercise(Path exercise) {
        return exerciseManager.getExercise(exercise, this);
    }

    @Override
    public void update(Observable o, Object arg) {
        sws.writeFinalTextFrame(new TransactionParser(TransactionPattern.RESPONSE_EXERCISE, arg).toJson());
    }
}
