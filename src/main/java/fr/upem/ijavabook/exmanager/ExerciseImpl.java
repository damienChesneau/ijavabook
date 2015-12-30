package fr.upem.ijavabook.exmanager;

import fr.upem.ijavabook.server.EventBusSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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
         *
         * @param htmlRepresentation java.lang.String representation of html content.
         */
        private EncapsulatePlayingData(String htmlRepresentation) {
            this(htmlRepresentation, 1);
        }

        /**
         * Represents file content and numbers of clients reading it.
         *
         * @param htmlRepresentation java.lang.String representation of html content.
         * @param nbClients          int define numbers of clients actually reading.
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
         *
         * @return new instance of EncapsulatePlayingData.
         */
        private EncapsulatePlayingData incrementClients() {
            return new EncapsulatePlayingData(htmlRepresentation, nbClients + 1);
        }

        /**
         * Decrement the number of clients actually reading.
         *
         * @return new instance of EncapsulatePlayingData.
         * @throws IllegalStateException if decrementing will pass the count to zero.
         */
        private EncapsulatePlayingData decrementClients() {
            if (nbClients == 1) {
                throw new IllegalStateException("Zero clients of this element.");
            }
            return new EncapsulatePlayingData(htmlRepresentation, nbClients - 1);
        }

        /**
         * Update the html representation.
         *
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
                try {
                    return new EncapsulatePlayingData(this.getHtmlOfAMarkdown(path));
                } catch (IOException e) {
                    throw new AssertionError("Can't get html content.", e);
                }
            } else {
                return encapsulatePlayingData.incrementClients();
            }
        }).htmlRepresentation;
    }

    @Override
    public void closeExercise(Path file) {
        if ((file = file.getFileName()) != null) {
            EncapsulatePlayingData encapsulatePlayingData = Objects.requireNonNull(htmlRepresentation.get(file), "The file you want's to close was never open :( ");
            try {
                htmlRepresentation.replace(file, Objects.requireNonNull(encapsulatePlayingData.decrementClients()));
            } catch (IllegalStateException e) {
                htmlRepresentation.remove(file);
            }
        }else {
            throw new IllegalArgumentException("Argument passed is not a directory.");
        }
    }

    private void updateExercise(Path file) {
        htmlRepresentation.computeIfPresent(file.getFileName(), (key, value) -> {
            try {
                String newTranslation = getHtmlOfAMarkdown(file);
                EncapsulatePlayingData encapsulatePlayingData = value.setHtmlRepresentation(newTranslation);
                eventBusSender.send(key, newTranslation);
                return encapsulatePlayingData;
            } catch (IOException e) {
                throw new IllegalStateException("Can't update with new html content", e);
            }
        });
    }

    /**
     * Start the watcher on the folder passed in constructor.
     */
    void startWatcher() {
        Thread threadWatcherOnRepository = new Thread(new Watcher(rootDirectory, this::updateExercise));
        threadWatcherOnRepository.start();
    }

    @Override
    public List<Path> getAllByDirectory() throws IOException {
        return Files.list(rootDirectory).filter(this::filterMarkdownFile).collect(Collectors.toList());
    }

    private boolean filterMarkdownFile(Path path) {
        String markdownExtension = ".text";
        return (path = path.getFileName()) == null || path.toString().contains(markdownExtension);
    }

    @Override
    public List<String> getFilesNamesWithoutExtension() throws IOException {
        int sizeOfExtension = 5;
        List<Path> allByDirectory = this.getAllByDirectory();
        return allByDirectory.stream()
                .filter(this::filterMarkdownFile)
                .map((path -> path.getFileName().toString()))
                .map((filename) -> filename.substring(0, filename.length() - sizeOfExtension))
                .collect(Collectors.<String>toList());
    }

    /**
     * Get the translation of the Markdown to HTML
     * @param file source path of the Markdown
     * @return HTML string
     * @throws IOException
     * @bug Sometimes Files.lines returns an empty string.
     *      We "patched" it with a while(Files.lines(..).isEmpty); but it's not a resolution of the bug.
     */
    private String getHtmlOfAMarkdown(Path file) throws IOException {
        Parser parser = new Parser(rootDirectory);
        String lines;
        synchronized (fileMonitor) {
            while((lines = Files.lines(rootDirectory.resolve(file.getFileName())).collect(Collectors.joining("\n"))).isEmpty());
        }
        return parser.parseMarkdown(lines);
    }

}
