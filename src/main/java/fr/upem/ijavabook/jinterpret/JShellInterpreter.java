package fr.upem.ijavabook.jinterpret;

import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation class for interpret a java code.
 * This class are immutable.
 *
 * @author Damien Chesneau
 */
class JShellInterpreter implements Interpreter {
    private final Path pNominal;
    private final Path pError;
    private final PrintStream sNominal;
    private final PrintStream sError;
    private final JShell jShell;
    private final Object monitor = new Object();

    /**
     * Create a JShellInterpreter
     *
     * @param pNominal path of a file witch contains the output of this Interpreter
     * @param pError   path of a file witch contains the all errors of this Interpreter
     * @param sNominal Stream of standard console.
     * @param sError   Stream of error console.
     * @param jShell   JShell of this Interpreter
     */
    JShellInterpreter(Path pNominal, Path pError, PrintStream sNominal, PrintStream sError, JShell jShell) {
        this.pNominal = Objects.requireNonNull(pNominal);
        this.pError = Objects.requireNonNull(pError);
        this.sNominal = Objects.requireNonNull(sNominal);
        this.sError = Objects.requireNonNull(sError);
        this.jShell = Objects.requireNonNull(jShell);
    }

    @Override
    public List<InterpretedLine> interpretAll(List<String> lines) {
        return lines.stream().map(this::interpret).collect(Collectors.<InterpretedLine>toList());
    }

    @Override
    public InterpretedLine interpret(String line) {
        SnippetEvent eval;
        synchronized (monitor) {
            eval = jShell.eval(line).get(0);
        }
        Exception e = eval.exception();
        String exception = "";
        if (eval.status() == Snippet.Status.REJECTED) {
            exception = "Invalid syntax.";
        } else if (e != null) {
            exception = getAllStackTrace(e);
        }
        return new InterpretedLine(eval.value(), exception/*,
                eval.status().name().equals("VALID")*/);
    }

    private String getAllStackTrace(Exception e) {
        StringBuilder stackTraceBuilder = new StringBuilder();
        stackTraceBuilder.append(e.toString()).append("\n");
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        for (int i = 0; i<stackTraceElements.length-2;i++) {
            stackTraceBuilder.append(stackTraceElements[i].toString()).append("\n");
        }
        return stackTraceBuilder.toString();
    }

    @Override
    public List<String> getOutput() throws IOException {
        synchronized (monitor) {
            return Files.readAllLines(pNominal);
        }
    }

    @Override
    public List<String> getErrors() throws IOException {
        synchronized (monitor) {
            return Files.readAllLines(pError);
        }
    }

    @Override
    public void close() {
        try {
            closeAndDestructFiles();
        } catch (IOException e) {
            throw new RuntimeException("Can't close interpreter :(.");
        }
    }

    private void closeAndDestructFiles() throws IOException {
        synchronized (monitor) {
            jShell.close();
            sError.close();
            sNominal.close();
            Files.deleteIfExists(pError);
            Files.deleteIfExists(pNominal);
        }
    }

    @Override
    public JunitTestResult test(String line) {
        boolean result;
        synchronized (monitor) {
            result = interpret(line).getException().isEmpty();
        }
        if (result) {
            return JunitTestResult.SUCCESS;
        }
        return JunitTestResult.FAIL;
    }
}
