package fr.upem.ijavabook.exmanager;

import org.junit.Test;

import java.nio.file.Path;
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
        Path path = Paths.get("markdown", "file.text");
        System.out.println(path.toAbsolutePath());
       // Exercises.getExerciseSrv().getExercise(path);
        assertEquals("", "");
    }
}
