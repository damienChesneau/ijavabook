package fr.upem.ijavabook.exmanager;

import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class ExerciseImpl implements ExerciseService {

    private final HashMap<String, HtmlObservable> htmlRepresentation = new HashMap<>();
    private final Object monitor = new Object();

    @Override
    public String getExercise(String file, Observer observer) {
        synchronized (monitor) {
            HtmlObservable observable = htmlRepresentation.computeIfAbsent(file, (str) -> new HtmlObservable(getHtmlOfAMarkdown(str)));
            observable.addObserver(observer);
            return observable.getHtml();
        }
    }

    public void updateExercise(String file) {
        synchronized (monitor) {
            htmlRepresentation.computeIfPresent("markdown/"+file, (key, value) -> {
                value.setHtmlTraduction(getHtmlOfAMarkdown(key));
                return value;
            });
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

    private String getHtmlOfAMarkdown(String file) {
        try {
            String value = Files.readAllLines(Paths.get(file)).stream().collect(Collectors.joining("\n"));
            return new PegDownProcessor().markdownToHtml(value);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public Thread start() {
        Thread t = new Thread(()-> {
            Watcher watcher = new Watcher(Paths.get("markdown/"), false);
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
}
