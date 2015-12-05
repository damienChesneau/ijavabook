package fr.upem.ijavabook.server;

import java.util.Arrays;
import java.util.Optional;

/**
 * Define formatted names to send in request/ responses.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
enum TransactionPattern {
    TYPE_PATTERN("t"), MESSAGE_PATTERN("m"), REQUEST_JAVA_CODE("jc"), REQUEST_ASK_EXERCISE("gete"),
    RESPONSE_EXERCISE("ex"), RESPONSE_CODE_OUTPUT("op"), RESPONSE_GET_ALL("rga");
    private String traduct;//Is a little expression to create little json Objects. This is faster.

    TransactionPattern(String traduct) {
        this.traduct = traduct;
    }

    String getTraduct() {
        return traduct;
    }

    /**
     * Get Object instance by her String traduction.
     * @param traduction
     * @return TransactionPattern instance.
     */
    static TransactionPattern getByTraduction(String traduction) {
         Optional<TransactionPattern> oTP = Arrays.stream(values())
                 .filter(item -> item.getTraduct().equals(traduction))
                 .findFirst();
        if(!oTP.isPresent()){
            throw new IllegalArgumentException("Please send a real type in parameter.");
        }
        return oTP.get();// TO REFORMAT
    }

}