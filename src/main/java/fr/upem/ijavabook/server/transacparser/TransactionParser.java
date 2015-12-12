package fr.upem.ijavabook.server.transacparser;

import fr.upem.ijavabook.jinterpret.InterpretedLine;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Encapsulate your data to send a formatted response.
 *
 * @author Damien Chesneau
 */
public class TransactionParser<T> {
    private final TransactionPattern type;
    private final T message;

    /**
     * @param type    of transaction.
     * @param message to send.
     */
    public TransactionParser(TransactionPattern type, T message) {
        this.type = Objects.requireNonNull(type);
        this.message = Objects.requireNonNull(message);
    }

    public T getMessage() {
        return message;
    }

    public TransactionPattern getType() {
        return type;
    }

    /**
     * Encode your instance to a json String.
     *
     * @return Json data.
     */
    public String toJson() {
        JsonObject json = new JsonObject();
        json.put(TransactionPattern.TYPE_PATTERN.getTranslation(), type.getTranslation());
        json.put(TransactionPattern.MESSAGE_PATTERN.getTranslation(), message);
        return json.encode();
    }

    /**
     * static factory to parseAsObject your json object to POJO.
     *
     * @param query
     * @return
     */
    public static TransactionParser parseAsObject(String query) {
        Objects.requireNonNull(query);
        JsonObject json = new JsonObject(query);
        String tpAsStr = json.getString(TransactionPattern.TYPE_PATTERN.getTranslation());
        String message = json.getString(TransactionPattern.MESSAGE_PATTERN.getTranslation());
        return new TransactionParser(TransactionPattern.getByTranslation(tpAsStr), message);
    }

    public static TransactionParser parseAsObject(LinkedHashMap query) {
        Objects.requireNonNull(query);
        String tpAsStr = String.valueOf(query.get(TransactionPattern.TYPE_PATTERN.getTranslation()));
        String message = String.valueOf(query.get(TransactionPattern.MESSAGE_PATTERN.getTranslation()));
        return new TransactionParser(TransactionPattern.getByTranslation(tpAsStr), message);
    }

    /**
     * static factory to parseAsObject your json object to POJO.
     *
     * @param query
     * @return
     */
    public static List<TransactionParser<String>> parseAsArray(String query) {
        Objects.requireNonNull(query);
        JsonArray json = new JsonArray(query);
        List<LinkedHashMap> list = json.getList();
        ArrayList<TransactionParser<String>> parsed = new ArrayList<>();
        for (LinkedHashMap lhp : list) {
            String message = String.valueOf(lhp.get("m"));
            TransactionPattern type = TransactionPattern.getByTranslation(String.valueOf(lhp.get("t")));
            parsed.add(new TransactionParser<String>(type, message));
        }

        return parsed;
    }

    public static BuilderJavaInterpreted builderJavaInterpreted(TransactionPattern type, String output) {
        return new BuilderJavaInterpreted(type, output);
    }

    public static BuilderJavaInterpreted builderJavaInterpreted(TransactionPattern type, List<String> output) {
        return new BuilderJavaInterpreted(type, output.stream().collect(Collectors.joining("</br>")));
    }

    public static class BuilderJavaInterpreted {
        private final TransactionPattern type;
        private String message;
        private List<InterpretedLine> ils;
        private InterpretedLine il;
        private String output;

        private BuilderJavaInterpreted(TransactionPattern type, String output) {
            this.type = Objects.requireNonNull(type);
            this.output = Objects.requireNonNull(output);
        }

        public BuilderJavaInterpreted setMessage(String message) {
            this.message = message;
            return this;
        }

        public BuilderJavaInterpreted setInterpretedLines(List<InterpretedLine> ils) {
            this.ils = ils;
            return this;
        }

        public BuilderJavaInterpreted setInterpretedLine(InterpretedLine il) {
            this.il = il;
            return this;
        }

        private JsonArray jsonArrayForLine(InterpretedLine il) {
            JsonArray ja = new JsonArray();
            ja.add(il.getValue());
            ja.add(il.isValid());
            return ja.add(il.getException());
        }

        public TransactionParser build() {
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

    public static <E> BuilderJavaList builderJavaList(TransactionPattern type) {
        return new BuilderJavaList<E>(type);
    }

    public static class BuilderJavaList<E> {
        private final TransactionPattern type;
        private List<E> genericList;

        private BuilderJavaList(TransactionPattern type) {
            this.type = Objects.requireNonNull(type);
        }

        public BuilderJavaList<E> setList(List<E> genericList) {
            this.genericList = Objects.requireNonNull(genericList);
            return this;
        }

        public TransactionParser<JsonArray> build() {
            JsonArray arrayAsJson = new JsonArray();
            genericList.forEach((itemGeneric) -> arrayAsJson.add(itemGeneric));
            return new TransactionParser(this.type, arrayAsJson);
        }
    }

}
