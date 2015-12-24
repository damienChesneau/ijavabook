package fr.upem.ijavabook.jinterpret;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Damien Chesneau
 */
class JShellInterpreter implements Interpreter {
    private final Path pNominal;
    private final Path pError;
    private final PrintStream sNominal;
    private final PrintStream sError;
    private final JShell jShell;

    JShellInterpreter(Path pNominal, Path pError, PrintStream sNominal, PrintStream sError, JShell jShell) {
        this.pNominal = Objects.requireNonNull(pNominal);
        this.pError = Objects.requireNonNull(pError);
        this.sNominal = Objects.requireNonNull(sNominal);
        this.sError = Objects.requireNonNull(sError);
        this.jShell = Objects.requireNonNull(jShell);
    }

    @Override
    public List<InterpretedLine> interpretAll(List<String> lines) {
        return lines.stream().map((line) -> interpret(line)).collect(Collectors.<InterpretedLine>toList());
    }

    @Override
    public InterpretedLine interpret(String line) {
        List<SnippetEvent> eval = jShell.eval(line);
        Exception e = eval.get(0).exception();
        return new InterpretedLine(eval.get(0).value(), (e != null) ? e.toString() : "",
                eval.get(0).status().name().equals("VALID"));
    }

    @Override
    public List<String> getOutput() {
        try {
            return Files.readAllLines(pNominal);
        } catch (IOException e) {
            throw new Error("Unable to get output. Please close and retry.");
        }
    }

    @Override
    public List<String> getErrors() {
        try {
            return Files.readAllLines(pError);
        } catch (IOException e) {
            throw new Error("Unable to get output of errors. Please close and retry.");
        }
    }

    @Override
    public void close() {
        jShell.close();
        sError.close();
        sNominal.close();
        try {
            Files.deleteIfExists(pError);
            Files.deleteIfExists(pNominal);
        } catch (IOException e) {
            throw new Error("Unable to clean temporary output files.");
        }
    }

    @Override
    public JunitTestResult test(String line) {
        if(interpret(line).getException().isEmpty()){
           return JunitTestResult.SUCCESS;
        }
        return JunitTestResult.FAIL;
    }
}
