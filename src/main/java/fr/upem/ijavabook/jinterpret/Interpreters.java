package fr.upem.ijavabook.jinterpret;

import jdk.jshell.JShell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Damien Chesneau
 */
public class Interpreters {
    /**
     * See static method :
     * public static Interpreter getJavaInterpreter();
     */
    private Interpreters() {
    }

    /**
     * Get implementation of java interpreter designed for are easy to use.
     *
     * @return java interpreter.
     * @throws Error if io exception in output files.
     */
    public static Interpreter getJavaInterpreter() {
        try {
            Path tmpFile = Files.createTempFile("tmp", ".jshell");
            Path tmpErrFile = Files.createTempFile("tmp", ".err.jshell");
            PrintStream sNominal = new PrintStream(Files.newOutputStream(tmpFile));
            PrintStream sError = new PrintStream(Files.newOutputStream(tmpErrFile));
            JShell jshell = JShell.builder().out(sNominal).err(sError).build();
            JShellInterpreter jShellInterpreter = new JShellInterpreter(tmpFile, tmpErrFile, sNominal, sError, jshell);
            jShellInterpreter.interpret("import org.junit.*;");
            return jShellInterpreter;
        } catch (IOException e) {
            throw new Error("Unable to get Java interpreter :(");
        }
    }
}
