package fr.upem.ijavabook.server;


import fr.upem.ijavabook.exmanager.Exercises;
import fr.upem.ijavabook.jinterpret.InterpretedLine;
import fr.upem.ijavabook.jinterpret.Interpreter;
import fr.upem.ijavabook.jinterpret.Interpreters;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
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
class ExerciseWebSockets {
    /**
     * Define all methods.
     */
    private final HashMap<TransactionPattern, Function<TransactionParser, String>> operations = new HashMap<>();
    private final ServerWebSocket sws;
    private final Interpreter interpreter = Interpreters.getJavaInterpreter();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final Observer observer = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            sws.writeFinalTextFrame(new TransactionParser(TransactionPattern.RESPONSE_EXERCISE, arg).toJson());
        }
    };

    /**
     * @param sws ServerWebSocket instance to write and recives datas.
     */
    ExerciseWebSockets(ServerWebSocket sws) {
        this.sws = Objects.requireNonNull(sws);
        this.operations.put(TransactionPattern.REQUEST_ASK_EXERCISE, this::requerstAnExercice);
        this.operations.put(TransactionPattern.REQUEST_JAVA_CODE, this::requerstAnJavaCode);
    }

    /**
     * Start method to use when a new connection comes.
     *
     * @param buf
     */
    public void start(Buffer buf) {
        threadPool.execute(()-> {
            TransactionParser tp = TransactionParser.parse(String.valueOf(buf));
            sws.writeFinalTextFrame(operations.get(tp.getType()).apply(tp));
        });
    }

    final String requerstAnExercice(TransactionParser<String> tp) {
        String exercise = getExercise(getExercicePath(tp.getMessage()));
        TransactionParser creator = new TransactionParser(TransactionPattern.RESPONSE_EXERCISE, exercise);
        //manageUpdatesOfExercises(exerciseP, tp);
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
        Exercises.getExerciseSrv().removeObserver(observer);
        return null;
    }

    private String getExercicePath(String exercise) {
        return "markdown/file" + exercise + ".text";
    }

    private String getExercise(String exercise) {
        return Exercises.getExerciseSrv().getExercise(exercise,observer);
    }

}
