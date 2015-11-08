package fr.upem.ijavabook.jinterpret;

import jdk.jshell.JShell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Interpreters {
    /**
     * Get implementation of java interpreter designed for are easy to use.
     * @return java interpreter.
     */
    public static Interpreter getJavaInterpreter() {
        try {
            Path tmpFile = Files.createTempFile("tmp", ".jshell");
            Path tmpErrFile = Files.createTempFile("tmp", ".err.jshell");
            PrintStream sNominal = new PrintStream(Files.newOutputStream(tmpFile));
            PrintStream sError = new PrintStream(Files.newOutputStream(tmpErrFile));
            JShell build = JShell.builder().out(sNominal).err(sError).build();
            return new JShellInterpreter(tmpFile, tmpErrFile, sNominal, sError, build);
        } catch (IOException e) {
            throw new Error("Unable to get Java interpreter :(");
        }
    }
}
