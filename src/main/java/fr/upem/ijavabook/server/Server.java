package fr.upem.ijavabook.server;

/**
 * Do action in server.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public interface Server {
    /**
     * Start server.
     */
    String start();


    /**
     * Ask an exercise to the server.
     *
     * @param name of the exercise
     * @return html of the exercise
     */
    String getExercise(String name);

    /**
     * Stop server.
     */
    void stop();
}
