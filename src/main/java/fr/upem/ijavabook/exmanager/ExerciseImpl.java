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
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Class allows to have communication with files.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ExerciseImpl implements ExerciseService {

    private final HashMap<Path, HtmlObservable> htmlRepresentation = new HashMap<>();
    private Thread watcher;
    private final Path rootDirectory;
    private final Object monitor = new Object();

    ExerciseImpl(Path rootDirectory){
        Objects.requireNonNull(rootDirectory);
        this.rootDirectory = rootDirectory;
    }



    @Override
    public String getExercise(Path file, Observer observer) {
        synchronized (monitor) {
            HtmlObservable observable = htmlRepresentation.computeIfAbsent(file.toAbsolutePath(), (str) -> new HtmlObservable(getHtmlOfAMarkdown(file)));
            observable.addObserver(observer);
            return observable.getHtml();
        }
    }

    public void updateExercise(Path file) {
        synchronized (monitor) {
            htmlRepresentation.computeIfPresent(file.toAbsolutePath(), (key, value) -> {
                value.setHtmlTranslation(getHtmlOfAMarkdown(file));
                return value;
            });
        }
    }

    @Override
    public void startWatcher() throws IllegalAccessException {
        if(watcher != null){
            throw new IllegalAccessException();
        }
        this.watcher = new Thread(()-> {
            Watcher watcher = new Watcher(rootDirectory, false);
            watcher.setOnUpdate(this::updateExercise);
            try {
                watcher.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch( InterruptedException e){
            }
        });
        watcher.start();
    }

    @Override
    public void stopWatcher() throws IllegalAccessException {
        if(watcher == null){
            throw new IllegalAccessException();
        }
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
            String value = Files.readAllLines(file).stream().collect(Collectors.joining("\n"));
            /*
                Here readAllLines return an empty string.
                That's strange because if the Path wasn't good it'll throw an exception but it's not the case.
                The Path is valid but I don't know why it return an empty string...
                Could you light me on it?
                By the way, it not bug every time, it's pretty random...
             */
            return new PegDownProcessor().markdownToHtml(value);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        htmlRepresentation.forEach((key,value)-> value.deleteObserver(observer));
    }
}
