package fr.upem.ijavabook.server;

import java.nio.file.Path;

/**
 * @author Damien Chesneau
 */
public interface EventBusSender {
    void send(Path file, String htmlContent);
}
