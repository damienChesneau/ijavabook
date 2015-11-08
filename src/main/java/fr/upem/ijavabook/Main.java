package fr.upem.ijavabook;

import fr.upem.ijavabook.server.Server;
import fr.upem.ijavabook.server.Servers;

/**
 * JShell Book program input.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Main {


    public static void main(String[] args) {
        Server srv = Servers.getServer();
        srv.start();
    }
}
