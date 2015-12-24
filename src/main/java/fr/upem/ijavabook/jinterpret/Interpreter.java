package fr.upem.ijavabook.jinterpret;

import java.util.List;

/**
 * @author Damien Chesneau
 */
public interface Interpreter extends AutoCloseable {
    /**
     * Interpret a single line of Java code.
     *
     * @param line Java code.
     * @return Java result or exception.
     */
    InterpretedLine interpret(String line);

    /**
     * Same as interpret method but for many lines.
     *
     * @param lines of Java code.
     * @return Java result or exception.
     */
    List<InterpretedLine> interpretAll(List<String> lines);

    /**
     * Get output of your code inserted.
     *
     * @return List of console lines.
     */
    List<String> getOutput();

    /**
     * Get output errors of your code inserted.
     *
     * @return List of console lines.
     */
    List<String> getErrors();

    /**
     * You need doc to this ?
     */
    @Override
    void close();

    JunitTestResult test(String line);
}
