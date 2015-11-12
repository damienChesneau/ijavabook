package fr.upem.ijavabook.exmanager;

import fr.upem.ijavabook.exmanager.parser.Parsers;

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
        String line1 = "![Alt text]\n";
        String value = Files.readAllLines(file).stream().collect(Collectors.joining("\n"));
        Parsers.markdownToHtml(value);
        return "<h1> Hello </h1>";
    }
}
