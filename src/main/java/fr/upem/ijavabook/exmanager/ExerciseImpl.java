package fr.upem.ijavabook.exmanager;

import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class allows to have communication with files.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ExerciseImpl implements ExerciseService {

    private final HashMap<Path, HtmlObservable> htmlRepresentation = new HashMap<>();
    private final Path rootDirectory;
    private final Object monitor = new Object();

    ExerciseImpl(Path rootDirectory){
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
    }

    @Override
    public String getExercise(Path file, Observer observer) {
        synchronized (monitor) {
            HtmlObservable observable = htmlRepresentation.computeIfAbsent(file, (str) -> new HtmlObservable(getHtmlOfAMarkdown(str)));
            observable.addObserver(observer);
            return observable.getHtml();
        }
    }

    public void updateExercise(Path file) {
        synchronized (monitor) {
            htmlRepresentation.computeIfPresent(file, (key, value) -> {
                value.setHtmlTranslation(getHtmlOfAMarkdown(key));
                return value;
            });
        }
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

    private String getHtmlOfAMarkdown(Path file) {
        try {
            String value = Files.readAllLines(file).stream().collect(Collectors.joining("\n"));
            return new PegDownProcessor().markdownToHtml(value);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public Thread start() {
        Thread t = new Thread(()-> {
            Watcher watcher = new Watcher(rootDirectory, false);
            watcher.setOnUpdate(this::updateExercise);
            try {
                watcher.start();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }

    @Override
    public void removeObserver(Observer observer) {
        htmlRepresentation.forEach((key,value)-> value.deleteObserver(observer));
    }
}
