package fr.upem.ijavabook;

import fr.upem.ijavabook.server.Server;
import fr.upem.ijavabook.server.Servers;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JShell Book program input.
 *
 * @author Damien Chesneau
 */
public class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Path markdownFolder = Paths.get(".").toAbsolutePath();
        Server srv = Servers.getServer(markdownFolder);
        String start = srv.start();
        System.out.println("Server started on : " + start);
    }
}
