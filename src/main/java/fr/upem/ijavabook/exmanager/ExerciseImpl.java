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
    private final String directoryPath;

    ExerciseImpl(String directoryPath){
        this.directoryPath = Objects.requireNonNull(directoryPath);
    }

    @Override
    public String getExercise(Path file) {
        return htmlRepresentation.computeIfAbsent(file.toString(), this::getHtmlOfAnMarkdown);
    }

    public void updateExercise(String file) {
        htmlRepresentation.computeIfPresent(file,(key,value)->getHtmlOfAnMarkdown(file));
    }

    @Override
    public List<Path> getAllByDirectory(Path path) {
        try {
            return Files.list(path).collect(Collectors.toList());
        } catch (IOException e) {
            Logger.getLogger(ExerciseImpl.class.getName()).log(Level.SEVERE, "Can't get all paths.");
            throw new AssertionError();
        }
    }

    private String getHtmlOfAnMarkdown(String file) {
        try {
            String value = Files.readAllLines(Paths.get(file)).stream().collect(Collectors.joining("\n"));
            return new PegDownProcessor().markdownToHtml(value);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }
/*
    private final void manageUpdatesOfExercises(Path exercice, TransactionParser<String> tp) {
        Path p = exercice.getParent().toAbsolutePath();
        Thread t = new Thread(watcher(p, sws, exercice, tp));
        t.start();
    }
    */
    Thread start() {
        Thread t = new Thread(() -> {
            Watcher watcher = new Watcher(Paths.get(directoryPath), false);
           watcher.setOnUpdate((str)-> updateExercise(str));
            try {
                watcher.start();
            } catch (IOException | InterruptedException e) {
            }
        });
        t.start();
        return t;
    }
}
