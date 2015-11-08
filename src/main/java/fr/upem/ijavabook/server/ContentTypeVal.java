package fr.upem.ijavabook.server;

/**
 * Represents content type values.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
enum ContentTypeVal {
    KEY_VALUE("content-type"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml");

    private final String content;

    ContentTypeVal(String content) {
        this.content = content;
    }

    String getContent() {
        return content;
    }
}
