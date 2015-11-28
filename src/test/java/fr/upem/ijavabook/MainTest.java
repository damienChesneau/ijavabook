package fr.upem.ijavabook;

import fr.upem.ijavabook.server.Server;
import fr.upem.ijavabook.server.Servers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * JShell Book program input.
 *
 * @author Damien Chesneau
 */
public class MainTest {

    private MainTest() {
    }

    public static void main(String[] args) {
        Path markdownFolder = Paths.get("markdown").toAbsolutePath();//TO BE UPDATE IN PROD
        Server srv = Servers.getServer(markdownFolder);
        String start = srv.start();
        System.out.println("Server started on : " + start);
        Scanner input = new Scanner(System.in);
        System.out.println("For stop the server, press Q");
        while(input.hasNext()){
            if(input.next().equals("Q")){
                srv.stop();
            }
        }
    }

}
