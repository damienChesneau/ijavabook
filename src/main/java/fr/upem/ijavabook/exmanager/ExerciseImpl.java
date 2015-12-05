package fr.upem.ijavabook.exmanager;

import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ExerciseImpl implements ExerciseService {

    private final HashMap<String, String> htmlRepresentation = new HashMap<>();
    private final Object monitor = new Object();

    @Override
    public String getExercise(String file) {
        synchronized (monitor) {
            return htmlRepresentation.computeIfAbsent(file, this::getHtmlOfAnMarkdown);
        }
    }

    public void updateExercise(String file) {
        synchronized (monitor) {
            htmlRepresentation.computeIfPresent(file, (key, value) -> getHtmlOfAnMarkdown(file));
        }
    }

   /* @Override
    public List<Path> getAllByDirectory(Path path) {
        try {
            return Files.list(path).collect(Collectors.toList());
        } catch (IOException e) {
            Logger.getLogger(ExerciseImpl.class.getName()).log(Level.SEVERE, "Can't get all paths.");
            throw new AssertionError();
        }
    }*/

    private String getHtmlOfAnMarkdown(String file) {
        try {
            String value = Files.readAllLines(Paths.get("markdown/file"+file+".text")).stream().collect(Collectors.joining("\n"));
            return new PegDownProcessor().markdownToHtml(value);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void start() {
        Watcher watcher = new Watcher(Paths.get("markdown/"), false);
        watcher.setOnUpdate((str)-> updateExercise(str));
        try {
            watcher.start();
        } catch (IOException | InterruptedException e) {
        }
    }
}
