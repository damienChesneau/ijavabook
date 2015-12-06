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
    private String transalation;//Is a little expression to create little json Objects. This is faster.

    TransactionPattern(String transalation) {
        this.transalation = transalation;
    }

    String getTranslation() {
        return transalation;
    }

    /**
     * Get Object instance by her String translation.
     * @param translation String of the pattern.
     * @return TransactionPattern instance.
     */
    static TransactionPattern getByTranslation(String translation) {
         Optional<TransactionPattern> oTP = Arrays.stream(values())
                 .filter(item -> item.getTranslation().equals(translation))
                 .findFirst();
        if(!oTP.isPresent()){
            throw new IllegalArgumentException("Please send a real type in parameter.");
        }
        return oTP.get();// TO REFORMAT
    }

}