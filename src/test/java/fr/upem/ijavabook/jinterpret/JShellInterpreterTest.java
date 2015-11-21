package fr.upem.ijavabook.jinterpret;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class JShellInterpreterTest {

    @Test
    public void devJShell() {
        try (Interpreter jsi = Interpreters.getJavaInterpreter()){
            List<String> interpret = jsi.interpretAll(Arrays.asList("int a = 2;", " System.out.println(a)"));
            System.out.println(jsi.getOutput());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
