package fr.upem.ijavabook.exmanager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface who allows all markdown files actions.
 *
 * @author Damien Chesneau
 */
public interface ExerciseService {
    /**
     * Get content of exercise.
     *
     * @param file java.nio.file.Path to text file.
     * @return HTML representation.
     */
    String playExercise(Path file);

    /**
     * When a client no longer need a exercise you can close it.
     *
     * @param file a java.nio.file.Path class represent the file to close.
     */
    void closeExercise(Path file);

    /**
     * Get all files and put her in a list.
     * All element is not a markdown file are filtred.
     *
     * @return a list of markdown files.
     * @throws IOException if we can't load files.
     */
    List<Path> getAllByDirectory() throws IOException;

    /**
     * Returns names of all exercises present in root directory.
     * All element is not a markdown file are filtred.
     *
     * @return a list of exercises names.
     * @throws IOException if we can't load files.
     */
    List<String> getFilesNamesWithoutExtension() throws IOException;
}
