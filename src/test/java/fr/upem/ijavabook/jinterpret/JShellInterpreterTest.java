package fr.upem.ijavabook.jinterpret;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * @author Damien Chesneau
 */
public class JShellInterpreterTest {
/*
    @Test
    public void returnOfIntegerInOutput() {
        try (Interpreter jsi = Interpreters.getJavaInterpreter()) {
            List<InterpretedLine> interpret = jsi.interpretAll(Arrays.asList("int a = 2;", " System.out.println(a)"));
            String out = (jsi.getOutput().stream().reduce("", (a, b) -> a + b));
            assertEquals(out, "2");
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void returnOfIntegerInErrorOutput() {
        try (Interpreter jsi = Interpreters.getJavaInterpreter()) {
            List<InterpretedLine> interpret = jsi.interpretAll(Arrays.asList("int a = 2;", " System.err.println(a)"));
            /*String out = (jsi.getErrors().stream().reduce("", (a, b) -> a + b));
            assertEquals("2", out);*/
/*        }
    }

    @Test
    public void arithmeticOp() {
        try (Interpreter jsi = Interpreters.getJavaInterpreter()) {
            InterpretedLine interpret = jsi.interpret("int a = 22*4;");
            System.out.println(interpret);
            assertEquals(String.valueOf(88), interpret.getValue());
        }
    }

    @Test
    public void classOperation() {
        try (Interpreter jsi = Interpreters.getJavaInterpreter()) {
            jsi.interpretAll(Arrays.asList("class A { public static int a() { return 55; }", "System.out.println(A.a())"));
            System.out.println(jsi.getOutput());
            assertEquals(Collections.emptyList(), jsi.getOutput());
        } catch (IOException e) {
            fail();
        }
    }
        */
}
