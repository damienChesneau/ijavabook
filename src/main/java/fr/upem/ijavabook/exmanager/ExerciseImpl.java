package fr.upem.ijavabook.exmanager;

import fr.upem.ijavabook.server.EventBusSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class allows to have communication with files.
 *
 * @author Damien Chesneau
 */
class ExerciseImpl implements ExerciseService {

    private final ConcurrentHashMap<Path, EncapsulatePlayingData> htmlRepresentation = new ConcurrentHashMap<>();
    private final Path rootDirectory;
    private final Object fileMonitor = new Object();
    private final EventBusSender eventBusSender;
    private final Parser parser = new Parser();

    /**
     * Create a new implementation of ExerciseService.
     *
     * @param rootDirectory  a java.nio.file.Path class represents the directory of exercises.
     * @param eventBusSender a fr.upem.ijavabook.server.EventBusSender class represent a way to notify update of a file.
     */
    ExerciseImpl(Path rootDirectory, EventBusSender eventBusSender) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        this.eventBusSender = Objects.requireNonNull(eventBusSender);
    }

    /**
     * Immutable class define to manage numbers of a file actually used.
     * With this files don't used will be remove of memory.
     */
    private static class EncapsulatePlayingData {
        private final String htmlRepresentation;
        private final int nbClients;

        /**
         * Represents file content and numbers of clients reading it.
         * With this constructor only one client number are define to 1.
         * @param htmlRepresentation java.lang.String representation of html content.
         */
        private EncapsulatePlayingData(String htmlRepresentation) {
            this(htmlRepresentation, 1);
        }

        /**
         * Represents file content and numbers of clients reading it.
         * @param htmlRepresentation java.lang.String representation of html content.
         * @param nbClients int define numbers of clients actually reading.
         */
        private EncapsulatePlayingData(String htmlRepresentation, int nbClients) {
            if (nbClients <= 0) {
                throw new IllegalArgumentException("Send a positive value.");
            }
            this.htmlRepresentation = Objects.requireNonNull(htmlRepresentation);
            this.nbClients = nbClients;
        }

        /**
         * Increment the number of clients actually reading.
         * @return new instance of EncapsulatePlayingData.
         */
        private EncapsulatePlayingData incrementClients() {
            return new EncapsulatePlayingData(htmlRepresentation, nbClients + 1);
        }
        /**
         * Decrement the number of clients actually reading.
         * @throws IllegalStateException if decrementing will pass the count to zero.
         * @return new instance of EncapsulatePlayingData.
         */
        private EncapsulatePlayingData decrementClients() {
            if (nbClients == 1) {
                throw new IllegalStateException("Zero clients of this element.");
            }
            return new EncapsulatePlayingData(htmlRepresentation, nbClients - 1);
        }
        /**
         * Update the html representation.
         * @return new instance of EncapsulatePlayingData.
         */
        private EncapsulatePlayingData setHtmlRepresentation(String htmlRepresentation) {
            return new EncapsulatePlayingData(htmlRepresentation, nbClients);
        }
    }

    @Override
    public String playExercise(Path file) {
        return htmlRepresentation.compute(file.getFileName(), (path, encapsulatePlayingData) -> {
            if (encapsulatePlayingData == null) {
                return new EncapsulatePlayingData(this.getHtmlOfAMarkdown(path));
            } else {
                return encapsulatePlayingData.incrementClients();
            }
        }).htmlRepresentation;
    }

    @Override
    public void closeExercise(Path file) {
        file = file.getFileName();
        EncapsulatePlayingData encapsulatePlayingData = Objects.requireNonNull(htmlRepresentation.get(file), "The file you want's to close was never open :( ");
        try {
            htmlRepresentation.replace(file, encapsulatePlayingData.decrementClients());
        } catch (IllegalStateException e) {
            htmlRepresentation.remove(file);
        }
    }

    private void updateExercise(Path file) {
        htmlRepresentation.computeIfPresent(file.getFileName(), (key, value) -> {
            String newTranslation = getHtmlOfAMarkdown(file);
            EncapsulatePlayingData encapsulatePlayingData = value.setHtmlRepresentation(newTranslation);
            eventBusSender.send(key, newTranslation);
            return encapsulatePlayingData;
        });
    }

    /**
     * Start the watcher on the folder passed in constructor.
     */
    public void startWatcher() {
        Thread threadWatcherOnRepository = new Thread(new Watcher(rootDirectory, this::updateExercise));
        threadWatcherOnRepository.start();
    }

    @Override
    public List<Path> getAllByDirectory() {
        try {
            return Files.list(rootDirectory).filter(this::filterMarkdownFile).collect(Collectors.toList());
        } catch (IOException e) {
            Logger.getLogger(ExerciseImpl.class.getName()).log(Level.SEVERE, "Can't get all paths.");
            throw new AssertionError();
        }
    }

    private boolean filterMarkdownFile(Path path) {
        String markdownExtension = ".text";
        return path.getFileName().toString().contains(markdownExtension);
    }

    @Override
    public List<String> getFilesNamesWithoutExtension() {
        int sizeOfExtension = 5;
        List<Path> allByDirectory = this.getAllByDirectory();
        return allByDirectory.stream()
                .filter(this::filterMarkdownFile)
                .map((path -> path.getFileName().toString()))
                .map((filename) -> filename.substring(0, filename.length() - sizeOfExtension))
                .collect(Collectors.<String>toList());
    }

    private String getHtmlOfAMarkdown(Path file) {
        try {
            String lines;
            synchronized (fileMonitor) {
                while((lines = Files.lines(rootDirectory.resolve(file.getFileName())).collect(Collectors.joining("\n"))).isEmpty());
            }
            return parser.parseMarkdown(lines);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
