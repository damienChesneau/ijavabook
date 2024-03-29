package fr.upem.ijavabook.jinterpret;

import java.io.IOException;
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
     * Get output of your code inserted.
     *
     * @return List of console lines.
     * @throws IOException if we can't access to output flux.
     */
    List<String> getOutput() throws IOException;

    /**
     * Get output errors of your code inserted.
     *
     * @return List of console lines.
     * @throws IOException if we can't access to output flux.
     */
    List<String> getErrors() throws IOException;

    /**
     * You need doc to this ?
     */
    @Override
    void close();

    /**
     * Test a line with the JUnit API
     *
     * @param line Java code to be tested
     * @return result of the Junit test
     */
    JunitTestResult test(String line);
}
