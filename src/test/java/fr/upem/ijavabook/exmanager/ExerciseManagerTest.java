package fr.upem.ijavabook.exmanager;

import org.junit.Test;

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
        Exercises.getExerciseSrv().getExercise(Paths.get("./test.text"));
        assertEquals("", "");
    }
}
