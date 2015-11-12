package fr.upem.ijavabook.exmanager.parser;

/**
 * Represent an Beans how can produce html code.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
interface HtmlProducer {
    /**
     * Run to get HTML code.
     * @return HTML representation of Bean called.
     */
    String produce();
}
