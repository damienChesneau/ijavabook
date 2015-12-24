package fr.upem.ijavabook.server.manageclients;

import fr.upem.ijavabook.jinterpret.InterpretedLine;
import fr.upem.ijavabook.jinterpret.Interpreter;
import fr.upem.ijavabook.jinterpret.Interpreters;
import fr.upem.ijavabook.jinterpret.JunitTestResult;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * This class represent a client and her interpreter instance.
 * Yes, this class is an interpreter but he access to client interpreter instance before.
 *
 * @author Damien Chesneau
 * @author Steeve Sivanantham
 */
public class Client implements Interpreter {
    private final Interpreter interpreter = Interpreters.getJavaInterpreter();
    private final Path exercice;

    public Client(Path exercise) {
        this.exercice = Objects.requireNonNull(exercise);
    }

    @Override
    public InterpretedLine interpret(String line) {
        return interpreter.interpret(line);
    }

    @Override
    public List<InterpretedLine> interpretAll(List<String> lines) {
        return interpreter.interpretAll(lines);
    }

    @Override
    public List<String> getOutput() {
        return interpreter.getOutput();
    }

    @Override
    public List<String> getErrors() {
        return interpreter.getErrors();
    }

    @Override
    public void close() {
        interpreter.close();
    }

    @Override
    public JunitTestResult test(String line) {
        return interpreter.test(line);
    }
}
