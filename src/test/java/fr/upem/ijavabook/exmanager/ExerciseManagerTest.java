package fr.upem.ijavabook.exmanager;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;

/**
 * JShell Book program input.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class ExerciseManagerTest {

    @Test
    public void dev() {
        try {
            Exercises.getExerciseSrv().getExercise(Paths.get("./test.text"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("", "");
    }
}
