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

    public void send(Path file, String htmlContent) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(htmlContent);
        String filename = file.getFileName().toString();
        filename = filename.substring(0, filename.length() - 5);
        eventBus.send(filename, new TransactionParser<>(TransactionPattern.RESPONSE_EXERCISE, htmlContent).toJson());
    }
}
