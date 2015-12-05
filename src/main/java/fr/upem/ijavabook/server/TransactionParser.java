package fr.upem.ijavabook.server;

import fr.upem.ijavabook.jinterpret.InterpretedLine;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Encapsulate your data to send a formatted response.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class TransactionParser<T> {
    private final TransactionPattern type;
    private final T message;

    /**
     * @param type    of transaction.
     * @param message to send.
     */
    TransactionParser(TransactionPattern type, T message) {
        this.type = Objects.requireNonNull(type);
        this.message = Objects.requireNonNull(message);
    }

    T getMessage() {
        return message;
    }

    TransactionPattern getType() {
        return type;
    }

    /**
     * Encode your instance to a json String.
     *
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
     *
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

    static class BuilderJavaInterpreted {
        private final TransactionPattern type;
        private String message;
        private List<InterpretedLine> ils;
        private InterpretedLine il;
        private String output;

        BuilderJavaInterpreted(TransactionPattern type, String output) {
            this.type = Objects.requireNonNull(type);
            this.output = Objects.requireNonNull(output);
        }

        BuilderJavaInterpreted setMessage(String message) {
            this.message = message;
            return this;
        }

        BuilderJavaInterpreted setInterpretedLines(List<InterpretedLine> ils) {
            this.ils = ils;
            return this;
        }

        BuilderJavaInterpreted setInterpretedLine(InterpretedLine il) {
            this.il = il;
            return this;
        }

        private JsonArray jsonArrayForLine(InterpretedLine il) {
            JsonArray ja = new JsonArray();
            ja.add(il.getValue());
            ja.add(il.isValid());
            return ja.add(il.getException());
        }

        TransactionParser build() {
            JsonArray ja = new JsonArray();
            JsonArray jm = new JsonArray();
            jm.add(output);
            ja.add(jm);
            if (message == null) {
                if (il != null) {
                    ja.add(jsonArrayForLine(il));
                } else {
                    List<JsonArray> str = ils.stream().map((li) -> jsonArrayForLine(li)).collect(Collectors.toList());
                    str.forEach((item) -> ja.add(item));
                }
            } else {
                ja.add(message);
            }
            return new TransactionParser(this.type, ja);
        }
    }

    static class BuilderJavaList<E> {
        private final TransactionPattern type;
        private List<E> genericList;

        BuilderJavaList(TransactionPattern type) {
            this.type = Objects.requireNonNull(type);
        }

        BuilderJavaList<E> setList(List<E> genericList) {
            this.genericList = Objects.requireNonNull(genericList);
            return this;
        }

        TransactionParser<JsonArray> build() {
            JsonArray arrayAsJson = new JsonArray();
            genericList.forEach((itemGeneric) -> arrayAsJson.add(itemGeneric));
            return new TransactionParser(this.type, arrayAsJson);
        }
    }

}
