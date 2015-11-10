package fr.damienchesneau.presentation;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * API to watch a repository with lambdas.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class Watcher {
    private final Path directory;
    private final boolean showHideFiles;
    private final HashMap<String, Consumer<String>> calls = new HashMap<>();
    private final ReentrantLock locker = new ReentrantLock();

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
        directory.register(watcher,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY);
        return watcher.take();
    }

    public void setOnDelete(Consumer<String> runDelete) {
        Objects.requireNonNull(runDelete);
        locker.lock();
        calls.put(ENTRY_DELETE.name(), runDelete);
        locker.unlock();
    }

    public void setOnNew(Consumer<String> runNew) {
        Objects.requireNonNull(runNew);
        locker.lock();
        calls.put(ENTRY_CREATE.name(), runNew);
        locker.unlock();
    }

    public void setOnUpdate(Consumer<String> runUpdate) {
        Objects.requireNonNull(runUpdate);
        locker.lock();
        calls.put(ENTRY_MODIFY.name(), runUpdate);
        locker.unlock();
    }

    public void start() throws IOException, InterruptedException {
        while (!Thread.interrupted()) {
            for (WatchEvent event : initializer().pollEvents()) {
                if (!Files.isHidden(Paths.get(event.context().toString()))) {
                    locker.lock();
                    action(event);
                    locker.unlock();
                }
            }
        }
    }

    private void action(WatchEvent event) {
        Consumer<String> consumer = calls.getOrDefault(event.kind().toString(), (cs) -> {
        });
        consumer.accept(event.context().toString());
    }

    public static void main(String[] args) {
        try {
            Watcher w = new Watcher(Paths.get("/home/damien/"), false);
            w.setOnDelete(System.out::println);
            w.setOnNew(System.out::println);
            w.setOnUpdate(System.out::println);
            w.start();
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
        } catch (InterruptedException e) {
        }
    }
}
