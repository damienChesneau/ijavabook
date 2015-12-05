package fr.upem.ijavabook.server;


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
        String exercise = getExercise(getExercicePath(tp.getMessage()),tp);
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

   /*
   private final void manageUpdatesOfExercises(Path exercice, TransactionParser<String> tp) {
        Path p = exercice.getParent().toAbsolutePath();
        Thread t = new Thread(watcher(p, sws, exercice, tp));
        t.start();
    }

    private Runnable watcher(Path directory, ServerWebSocket sws, Path exercise, TransactionParser<String> tp) {
        return () -> {
            Watcher watcher = new Watcher(directory, false);
            watcher.setOnUpdate(watcherOnExercice(sws, exercise, tp));
            try {
                watcher.start();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();//NO PROBEM.
            }
        };
    }

    private Consumer<String> watcherOnExercice(ServerWebSocket sws, Path exercise, TransactionParser<String> tp) {
        return (filename) -> {
            if (filename.equals(exercise.getFileName().toString())) {
                openedExercices.updateExercise(exercise);
                sws.writeFinalTextFrame(requerstAnExercice(tp));
            }
        };
    }
    */

    /**
     * To use for close interpreter instance.
     *
     * @param voiD instance
     * @return NPE
     */
    public ServerWebSocket onClose(Void voiD) {
        interpreter.close();
        threadPool.shutdown();
        return null;
    }

    private String getExercicePath(String exercise) {
       /* Path exercice = Paths.get("markdown/file" + exercise + ".text");//to update
        if (!Files.exists(exercice)) {
            sws.writeFinalTextFrame("ERROR->USE OTHER EXERCISE.");
            throw new AssertionError();
        }
        return exercice;*/
        return "markdown/file" + exercise + ".text";
    }

    private String getExercise(String exercise, TransactionParser<String> tp) {
        return null;
    }

    public void updateWebSock(String arg) {

    }
}
