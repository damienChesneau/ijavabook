package fr.upem.ijavabook.exmanager.parser;

/**
 * Interface can traduce content to an other.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
interface Parser {
    /**
     * With this method you can parse your content to all you need.
     * @param java.lang.String of content to parse.
     * @return java.lang.String parsed content.
     */
    String parse(String content);
}