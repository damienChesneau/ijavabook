package fr.upem.ijavabook.exmanager;

import fr.upem.ijavabook.server.EventBusSender;
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
 * @author Damien Chesneau
 */
class ExerciseImpl implements ExerciseService {

    private final HashMap<Path, HtmlObservable> htmlRepresentation = new HashMap<>();
    private final Thread watcher;
    private final Path rootDirectory;
    private final Object monitor = new Object();
    private final Object fileMonitor = new Object();
    private final EventBusSender eventBusSender;

    ExerciseImpl(Path rootDirectory, EventBusSender eventBusSender) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        this.eventBusSender = Objects.requireNonNull(eventBusSender);
        this.watcher = Objects.requireNonNull(new Thread(new Watcher(rootDirectory, this::updateExercise)));
        // to be reformated with an other englobing class.
    }

    @Override
    public String getExercise(Path file, Observer observer) {
        HtmlObservable html;
        synchronized (monitor) {
            html = htmlRepresentation.computeIfAbsent(file.toAbsolutePath(), (str) -> new HtmlObservable(getHtmlOfAMarkdown(str)));
        }
        return html.getHtml();
    }

    private void updateExercise(Path file) {
        String trad = getHtmlOfAMarkdown(file);
        synchronized (monitor) {
            htmlRepresentation.computeIfPresent(file.toAbsolutePath(), (key, value) -> {
                value.setHtmlTranslation(trad);
                eventBusSender.send(key, trad);
                return value;
            });
        }
    }

    @Override
    public void startWatcher() {
        watcher.start();
    }

    @Override
    public void stopWatcher() {
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

    @Override
    public List<String> getFilesNamesWithoutExtension() {
        List<Path> allByDirectory = this.getAllByDirectory(rootDirectory);
        return allByDirectory.stream()
                .map((path -> path.getFileName().toString()))
                .map((filename) -> filename.substring(0, filename.length() - 5))
                .collect(Collectors.<String>toList());

    }

    private String getHtmlOfAMarkdown(Path file) {
        try {
            String lines;
            synchronized (fileMonitor) {
                lines = Files.lines(file).collect(Collectors.joining("\n"));
            }
            return Parsers.parseMarkdown(lines);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        synchronized (monitor) {
            htmlRepresentation.forEach((key, value) -> value.deleteObserver(observer));
        }
    }
}
