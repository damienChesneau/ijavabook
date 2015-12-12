package fr.upem.ijavabook.server.manageclients;

import fr.upem.ijavabook.jinterpret.InterpretedLine;
import fr.upem.ijavabook.jinterpret.Interpreter;
import fr.upem.ijavabook.jinterpret.Interpreters;

import java.util.List;

/**
 * @author Damien Chesneau
 * @author Steeve Sivanantham
 */
public class Client implements Interpreter {
    private final Interpreter interpreter = Interpreters.getJavaInterpreter();

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
}
