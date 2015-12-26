package fr.upem.ijavabook.server;

import java.nio.file.Path;

/**
 * Interface witch contains the send method for an eventbus.
 *
 * @author Damien Chesneau
 */
public interface EventBusSender {
    void send(Path file, String htmlContent);
}
