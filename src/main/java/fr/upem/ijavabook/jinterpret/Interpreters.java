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
    private Interpreters() {
    }

    /**
     * Get implementation of java interpreter designed for are easy to use.
     * @throws Error if io exception in output files.
     * @return java interpreter.
     */
    public static Interpreter getJavaInterpreter() {
        try {
            Path tmpFile = Files.createTempFile("tmp", ".jshell");
            Path tmpErrFile = Files.createTempFile("tmp", ".err.jshell");
            PrintStream sNominal = new PrintStream(Files.newOutputStream(tmpFile));
            PrintStream sError = new PrintStream(Files.newOutputStream(tmpErrFile));
            JShell jshell = JShell.builder().out(sNominal).err(sError).build();
            return new JShellInterpreter(tmpFile, tmpErrFile, sNominal, sError, jshell);
        } catch (IOException e) {
            throw new Error("Unable to get Java interpreter :(");
        }
    }
}
