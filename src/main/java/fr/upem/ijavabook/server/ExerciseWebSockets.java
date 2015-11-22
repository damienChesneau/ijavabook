package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.Exercises;
import fr.upem.ijavabook.jinterpret.InterpretedLine;
import fr.upem.ijavabook.jinterpret.Interpreter;
import fr.upem.ijavabook.jinterpret.Interpreters;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Define all request/responses to client and server for exercises socket.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ExerciseWebSockets {
    /**
     * Define all methods.
     */
    private final HashMap<TransactionPattern, Function<TransactionParser, String>> operations = new HashMap<>();
    private final ServerWebSocket sws;
    private final Interpreter interpreter = Interpreters.getJavaInterpreter();

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
     * @param buf
     */
    public void start(Buffer buf) {
        TransactionParser tp = TransactionParser.parse(String.valueOf(buf));
        sws.writeFinalTextFrame(operations.get(tp.getType()).apply(tp));
    }

    private final String requerstAnExercice(TransactionParser<String> tp) {
        String exercise = getExercise(tp.getMessage());
        TransactionParser creator = new TransactionParser(TransactionPattern.RESPONSE_EXERCISE, exercise);
        return creator.toJson();
    }

    private final String requerstAnJavaCode(TransactionParser<String> tp) {
        InterpretedLine interpret = interpreter.interpret(tp.getMessage());
        String reduce = interpreter.getOutput().stream().collect(Collectors.joining("<br/>"));
        TransactionParser c = new TransactionParser.
                BuilderJavaInterpreted(TransactionPattern.RESPONSE_CODE_OUTPUT,reduce)
                .setInterpretedLine(interpret).build();
        return c.toJson();
    }

    /**
     * To use for close interpreter instance.
     * @param Void instance
     * @return NPE
     */
    public ServerWebSocket onClose(Void voiD) {
        interpreter.close();
        return null;
    }

    private String getExercise(String exerise) {
        Path exercice = Paths.get("markdown/file" + exerise + ".text");//to update
        if (!Files.exists(exercice)) {
            return "ERROR->USE OTHER EXERCISE.";
        }
        try {
            return Exercises.getExerciseSrv().getExercise(exercice);
        } catch (IOException e) {
            return "ERRROR->RETRY";
        }
    }

}
