package fr.upem.ijavabook.server.transacparser;

import fr.upem.ijavabook.jinterpret.InterpretedLine;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * @param query Request to parse as an Json object
     * @return return the corresponding transaction parser
     */
    public static <T> TransactionParser<T> parseAsObject(String query) {
        Objects.requireNonNull(query);
        JsonObject json = new JsonObject(query);
        String tpAsStr = json.getString(TransactionPattern.TYPE_PATTERN.getTranslation());
        try {
            JsonArray jsonArray = json.getJsonArray(TransactionPattern.MESSAGE_PATTERN.getTranslation());
            @SuppressWarnings("unchecked")
            T list = (T) jsonArray.getList();
            return new TransactionParser<>(TransactionPattern.getByTranslation(tpAsStr), list);
        } catch (ClassCastException e) {
            @SuppressWarnings("unchecked")
            T message = (T) json.getValue(TransactionPattern.MESSAGE_PATTERN.getTranslation());
            return new TransactionParser<>(TransactionPattern.getByTranslation(tpAsStr), message); // TO REFORMAT
        }
    }

    /*public static TransactionParser parseAsObject(LinkedHashMap query) {
        Objects.requireNonNull(query);
        String tpAsStr = String.valueOf(query.get(TransactionPattern.TYPE_PATTERN.getTranslation()));
        String message = String.valueOf(query.get(TransactionPattern.MESSAGE_PATTERN.getTranslation()));
        return new TransactionParser(TransactionPattern.getByTranslation(tpAsStr), message);
    }*/

    /**
     * static factory to parseAsObject your json object to POJO.
     *
     * @param query Request to pars as a json array object.
     * @return return the corresponding transaction parser
     */
    public static List<TransactionParser<String>> parseAsArray(String query) {
        Objects.requireNonNull(query);
        JsonArray json = new JsonArray(query);
        @SuppressWarnings("unchecked")//Yes i say what i do.
        List<LinkedHashMap<String,String>> list = (List<LinkedHashMap<String, String>>) json.getList();
        ArrayList<TransactionParser<String>> parsed = new ArrayList<>();
        for (LinkedHashMap<String, String> lhp : list) {
            String message = String.valueOf(lhp.get("m"));
            TransactionPattern type = TransactionPattern.getByTranslation(String.valueOf(lhp.get("t")));
            parsed.add(new TransactionParser<>(type, message));
        }
        return parsed;
    }

    /*public static BuilderJavaInterpreted builderJavaInterpreted(TransactionPattern type, String output) {
        return new BuilderJavaInterpreted(type, output);
    }*/

    /**
     * Builde a new BuilderJavaInterpreted
     *
     * @param type   type of the transactionPattern
     * @param output everylines of the interpreted output.
     * @return a new instance of BuilderJavaInterpreted.
     */
    public static <T> BuilderJavaInterpreted<T> builderJavaInterpreted(TransactionPattern type, List<String> output) {
        return new BuilderJavaInterpreted<>(type, output.stream().collect(Collectors.joining("</br>")));
    }

    /**
     * Builder class for transaction of InterpretedLine
     */
    public static class BuilderJavaInterpreted<T> {
        private final TransactionPattern type;
        /*private String message;
        private List<InterpretedLine> ils;*/
        private InterpretedLine il;
        private String output;

        private BuilderJavaInterpreted(TransactionPattern type, String output) {
            this.type = Objects.requireNonNull(type);
            this.output = Objects.requireNonNull(output);
        }

        /*public BuilderJavaInterpreted setMessage(String message) {
            this.message = message;
            return this;
        }

        public BuilderJavaInterpreted setInterpretedLines(List<InterpretedLine> ils) {
            this.ils = ils;
            return this;
        }*/

        public BuilderJavaInterpreted<T> setInterpretedLine(InterpretedLine il) {
            this.il = il;
            return this;
        }

        private JsonArray jsonArrayForLine(InterpretedLine il) {
            JsonArray ja = new JsonArray();
            String exception = il.getException();
            String value = il.getValue();
            ja.add(value).add(!value.isEmpty() | exception.isEmpty()).add(il.getException());
            return ja;
        }

        /**
         * Build the TransactionParser
         *
         * @return TransactionParser with the current states.
         */
        public TransactionParser<JsonArray> build() {
            JsonArray ja = new JsonArray();
            JsonArray jm = new JsonArray();
            jm.add(output);
            ja.add(jm);
            /*if (message == null) {
                if (il != null) {*/
            ja.add(jsonArrayForLine(il));
               /* } else {
                    List<JsonArray> str = ils.stream().map((li) -> jsonArrayForLine(li)).collect(Collectors.toList());
                    str.forEach((item) -> ja.add(item));
                }
            } else {
                ja.add(message);
            }*/
            return new TransactionParser<>(this.type, ja);
        }
    }

    /**
     * Build a new BuilderJavaList
     *
     * @param type type of transacctionPattern
     * @return a new builder
     */
    public static BuilderJavaList builderJavaList(TransactionPattern type) {
        return new BuilderJavaList(type);
    }

    public static class BuilderJavaList {
        private final TransactionPattern type;
        private List<String> genericList;

        private BuilderJavaList(TransactionPattern type) {
            this.type = Objects.requireNonNull(type);
        }


        public BuilderJavaList setStringList(List<String> genericList) {
            this.genericList = Objects.requireNonNull(genericList);
            return this;
        }


        /**
         * Build the TransactionParser
         *
         * @return TransactionParser with the current states.
         */
        public TransactionParser<JsonArray> build() {
            JsonArray arrayAsJson = new JsonArray();
            genericList.forEach(arrayAsJson::add);
            return new TransactionParser<>(this.type, arrayAsJson);
        }
    }

}
