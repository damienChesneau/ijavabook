package fr.upem.ijavabook.exmanager;

import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ExerciseImpl implements ExerciseService {

    @Override
    public String getExercise(Path file) throws IOException {
        String value = Files.readAllLines(file).stream().collect(Collectors.joining("\n"));
        return new PegDownProcessor().markdownToHtml(value);
    }
}
