package fr.upem.ijavabook.server;

import fr.upem.ijavabook.server.transacparser.TransactionParser;
import fr.upem.ijavabook.server.transacparser.TransactionPattern;
import io.vertx.core.eventbus.EventBus;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Immmutable class contains the send method for an eventbus.
 *
 * @author Damien Chesneau
 */
public class EventBusSender {
    private final EventBus eventBus;

    /**
     * Create an EventBusSender
     *
     * @param eventBus the eventBus to associate to de EventBusSender.
     */
    EventBusSender(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus);
    }

    /**
     * Send an update of an exercise.
     * @param file name of the file
     * @param htmlContent html of this file.
     */
    public void send(Path file, String htmlContent) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(htmlContent);
        String filename = file.toString();
        filename = filename.substring(0, filename.length() - 5);
        eventBus.publish(filename, new TransactionParser<>(TransactionPattern.RESPONSE_EXERCISE, htmlContent).toJson());
    }
}
