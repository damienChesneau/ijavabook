package fr.upem.ijavabook.server;

/**
 * Represents content type values.
 *
 * @author Damien Chesneau
 */
enum ContentTypeVal {
    KEY_VALUE("content-type"), APPLICATION_JSON("application/json");

    private final String content;

    ContentTypeVal(String content) {
        this.content = content;
    }

    String getContent() {
        return content;
    }
}
