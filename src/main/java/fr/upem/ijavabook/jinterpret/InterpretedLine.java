package fr.upem.ijavabook.jinterpret;

import java.util.Objects;

/**
 * @author Damien Chesneau
 */
public class InterpretedLine {
    //private final boolean valid;
    private final String value;
    private final String exception;

    /**
     * @param value     Of interpreted expression.
     * @param exception If have an exception.
     */
    InterpretedLine(String value, String exception/*, boolean valid*/) {
        //this.valid = valid;
        this.value = (value == null) ? "" : value;
        this.exception = Objects.requireNonNull(exception);
    }

    public String getException() {
        return exception;
    }

    public String getValue() {
        return value;
    }

   /* public boolean isValid() {
        return valid;
    }*/
}
