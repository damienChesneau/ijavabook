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
        SnippetEvent eval = jShell.eval(line).get(0);
        Exception e = eval.exception();
        String exception = "";
        if (eval.status() == Snippet.Status.REJECTED) {
            exception = "Invalid syntax.";
        } else if (e != null) {
            exception = e.toString();
        }
        return new InterpretedLine(eval.value(), exception/*,
                eval.status().name().equals("VALID")*/);
    }

    @Override
    public List<String> getOutput() throws IOException {
        return Files.readAllLines(pNominal);
    }

    @Override
    public List<String> getErrors() throws IOException {
        return Files.readAllLines(pError);
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
        jShell.close();
        sError.close();
        sNominal.close();
        Files.deleteIfExists(pError);
        Files.deleteIfExists(pNominal);
    }

    @Override
    public JunitTestResult test(String line) {
        if (interpret(line).getException().isEmpty()) {
            return JunitTestResult.SUCCESS;
        }
        return JunitTestResult.FAIL;
    }
}
