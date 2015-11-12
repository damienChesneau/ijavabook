package fr.upem.ijavabook;

import fr.upem.ijavabook.exmanager.Exercises;
import fr.upem.ijavabook.server.Server;
import fr.upem.ijavabook.server.Servers;

import java.io.IOException;
import java.net.BindException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JShell Book program input.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Main {


    public static void main(String[] args) {
        Server srv = Servers.getServer();
        try {
            String start = srv.start();
            System.out.println("Server started on : " + start);
            Path path = Paths.get("markdown/file.text");
            System.out.println(path.toAbsolutePath());
            String exercise = Exercises.getExerciseSrv().getExercise(path);
            System.out.println(exercise);
        } catch (BindException e) {
            System.err.println("err");
        } catch (IOException e) {
            System.out.println("Unable to get your .text file.");
        }
    }
}
