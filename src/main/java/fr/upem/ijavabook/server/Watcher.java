package fr.upem.ijavabook.server;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * API to watch a repository with lambdas.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class Watcher {
    private final Path directory;
    private final boolean showHideFiles;
    private final HashMap<String, Consumer<String>> calls = new HashMap<>();

    public Watcher(Path directory) {
        this(directory, true);
    }

    public Watcher(Path directory, boolean showHideFiles) {
        this.showHideFiles = showHideFiles;
        Objects.requireNonNull(directory);
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Please send a path of a directoy.");
        }
        this.directory = directory;
    }

    private WatchKey initializer() throws IOException, InterruptedException {
        WatchService watcher = directory.getFileSystem().newWatchService();
        directory.register(watcher,ENTRY_MODIFY);
        return watcher.take();
    }

    public void setOnUpdate(Consumer<String> runUpdate) {
        Objects.requireNonNull(runUpdate);
        calls.put(ENTRY_MODIFY.name(), runUpdate);
    }

    public void start() throws IOException, InterruptedException {
        while (!Thread.interrupted()) {
            for (WatchEvent event : initializer().pollEvents()) {
                if (testHiddedFiles().test(event.context().toString())) {
                    callUserLambda(event);
                }
            }
        }
    }

    private Predicate<String> testHiddedFiles() {
        return (str) -> {
            if (!showHideFiles) {
                try {
                    return (Files.isHidden(Paths.get(str))) ? false : true;
                } catch (IOException e) { // If error, no problem we still print all.
                }
            }
            return true;
        };
    }

    private void callUserLambda(WatchEvent event) {
        Consumer<String> consumer = calls.getOrDefault(event.kind().toString(), (cs) -> {
        });
        consumer.accept(event.context().toString());
    }

    public static void main(String[] args) {
        try {
            Watcher w = new Watcher(Paths.get("/home/damien/"), false);
            w.setOnUpdate(System.out::println);
            w.start();
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
        } catch (InterruptedException e) {
        }
    }
}
