package fr.upem.ijavabook;

import fr.upem.ijavabook.exmanager.Exercises;
import fr.upem.ijavabook.server.Server;
import fr.upem.ijavabook.server.Servers;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JShell Book program input.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Path markdownFolder = Paths.get("markdown").toAbsolutePath();//TO BE UPDATE IN PROD
        Exercises.start(markdownFolder);
        Server srv = Servers.getServer(markdownFolder);
        String start = srv.start();
        System.out.println("Server started on : " + start);
    }
}
