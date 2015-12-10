package fr.upem.ijavabook.exmanager;

import javafx.beans.Observable;
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
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ExerciseImpl implements ExerciseService {

    private final HashMap<Path, HtmlObservable> htmlRepresentation = new HashMap<>();
    private final Thread watcher;
    private final Path rootDirectory;
    private final Object monitor = new Object();
    private final Object fileMonitor = new Object();

    ExerciseImpl(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        this.watcher = Objects.requireNonNull(new Thread(new Watcher(rootDirectory, this::updateExercise)));
        // to be reformated with an other englobing class.
    }

    @Override
    public String getExercise(Path file, Observer observer) {
        HtmlObservable html;
        synchronized (monitor){
            html = htmlRepresentation.computeIfAbsent(file.toAbsolutePath(),(str)->new HtmlObservable(getHtmlOfAMarkdown(str)));
        }
        html.addObserver(observer);
        return html.getHtml();
    }

    private void updateExercise(Path file) {
        synchronized (monitor) {
                htmlRepresentation.computeIfPresent(file.toAbsolutePath(),(key,value)-> {
                value.setHtmlTranslation(getHtmlOfAMarkdown(key));
                return value;
            });
        }
    }

    @Override
    public void startWatcher() throws IllegalAccessException {
        watcher.start();
    }

    @Override
    public void stopWatcher() throws IllegalAccessException {
        watcher.interrupt();
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
            List<String> lines;
            synchronized (fileMonitor) {
                lines = Files.readAllLines(file);
            }
            return new PegDownProcessor().markdownToHtml(lines.stream().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        synchronized (monitor) {
            htmlRepresentation.forEach((key, value) -> value.deleteObserver(observer));
        }
    }
}
