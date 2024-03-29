package fr.upem.ijavabook.server.transacparser;

import java.util.Arrays;
import java.util.Optional;

/**
 * Define formatted names to send in request/ responses.
 * This way allow to have short types files are finest.
 * @author Damien Chesneau
 */
public enum TransactionPattern {
    TYPE_PATTERN("t"), MESSAGE_PATTERN("m"), REQUEST_JAVA_CODE("jc"), REQUEST_ASK_EXERCISE("gete"), RESPONSE_EXERCISE("ex"),
    RESPONSE_TOKEN_EXERCISE("tex"), RESPONSE_CODE_OUTPUT("op"), RESPONSE_GET_ALL("rga"), RESPONSE_NEW_TOKEN("nt"),
    REQUEST_TOKEN("to"), REQUEST_CLOSE_EXERCISE("cex"), RESPONSE_JUNIT_RESULT("rjr"), REQUEST_JUNIT_TEST("rjt"),
    RESPONSE_ERROR("er");
    private String transalation;//Is a little expression to create little json Objects. This is faster.

    TransactionPattern(String transalation) {
        this.transalation = transalation;
    }

    String getTranslation() {
        return transalation;
    }

    /**
     * Get Object instance by her String translation.
     *
     * @param translation String of the pattern.
     * @return TransactionPattern instance.
     */
    static TransactionPattern getByTranslation(String translation) {
        Optional<TransactionPattern> oTP = Arrays.stream(values())
                .filter(item -> item.getTranslation().equals(translation))
                .findFirst();
        if (!oTP.isPresent()) {
            throw new IllegalArgumentException("Please send a real type in parameter.");
        }
        return oTP.get();
    }

}