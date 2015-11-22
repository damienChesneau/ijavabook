package fr.upem.ijavabook.server;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Encapsulate your data to send a formatted response.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class TransactionParser {
    private final TransactionPattern type;
    private final String message;

    /**
     *
     * @param type of transaction.
     * @param message to send.
     */
    TransactionParser(TransactionPattern type, String message) {
        this.type = Objects.requireNonNull(type);
        this.message = Objects.requireNonNull(message);
    }

    String getMessage() {
        return message;
    }

    TransactionPattern getType() {
        return type;
    }

    /**
     * Encode your instance to a json String.
     * @return Json data.
     */
    String toJson() {
        JsonObject json = new JsonObject();
        json.put(TransactionPattern.TYPE_PATTERN.getTraduct(), type.getTraduct());
        json.put(TransactionPattern.MESSAGE_PATTERN.getTraduct(), message);
        return json.encode();
    }

    /**
     * statc factory to parse your json object to POJO.
     * @param query
     * @return
     */
    static TransactionParser parse(String query) {
        Objects.requireNonNull(query);
        JsonObject json = new JsonObject(query);
        String tpAsStr = json.getString(TransactionPattern.TYPE_PATTERN.getTraduct());
        String message = json.getString(TransactionPattern.MESSAGE_PATTERN.getTraduct());
        return new TransactionParser(TransactionPattern.getByTraduction(tpAsStr), message);
    }
}
