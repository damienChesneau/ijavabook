package fr.upem.ijavabook;

import fr.upem.ijavabook.server.Server;
import fr.upem.ijavabook.server.Servers;

/**
 * JShell Book program input.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Server srv = Servers.getServer();
        String start = srv.start();
        System.out.println("Server started on : " + start);
    }
}
