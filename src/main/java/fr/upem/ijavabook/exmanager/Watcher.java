package fr.upem.ijavabook.exmanager;

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
 * @author Damien Chesneau
 */
class Watcher implements Runnable {
    private final Path directory;
    private final boolean showHideFiles;
    private final HashMap<String, Consumer<Path>> calls;

    Watcher(Path directory, Consumer<Path> onUpdate) {
        this(directory, onUpdate, false);
    }

    Watcher(Path directory, Consumer<Path> onUpdate, boolean showHideFiles) {
        if (!Files.isDirectory(Objects.requireNonNull(directory))) {
            throw new IllegalArgumentException("Please send a path of a directoy.");
        }
        this.showHideFiles = showHideFiles;
        this.directory = directory;
        this.calls = new HashMap<>();
        this.calls.put(ENTRY_MODIFY.name(), onUpdate);
    }

    private WatchKey initializer() throws IOException, InterruptedException {
        WatchService watcher = directory.getFileSystem().newWatchService();
        directory.register(watcher, ENTRY_MODIFY);
        return watcher.take();
    }

    /*public void setOnUpdate(Consumer<Path> runUpdate) {
        Objects.requireNonNull(runUpdate);
        calls.put(ENTRY_MODIFY.name(), runUpdate);
    }*/

    private void launch() throws IOException, InterruptedException {
        while (!Thread.interrupted()) {
            initializer().pollEvents().forEach(event -> {
                if (testHiddenFiles().test(event.context().toString())) {
                    callUserLambda(event);
                }
            });
        }
    }

    private Predicate<String> testHiddenFiles() {
        return (str) -> {
            if (!showHideFiles) {
                try {
                    return !(Files.isHidden(Paths.get(str)));
                } catch (IOException e) { // If error, no problem we still print all.
                }
            }
            return true;
        };
    }

    private void callUserLambda(WatchEvent event) {
        Consumer<Path> consumer = calls.getOrDefault(event.kind().toString(), (cs) -> {
        });
        Path resolve = directory.resolve((event.context().toString())).normalize();
        if (Files.exists(resolve)) {
            consumer.accept(resolve);
        }
    }

    @Override
    public void run() {
        try {
            this.launch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            return;
        }
    }
}
