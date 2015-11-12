package fr.upem.ijavabook.exmanager.parser;

/**
 * Static class allows to parse your needed content.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class Parsers {
    private Parsers() {
    }

    /**
     * Markdown parser.
     * @param content to traduce.
     * @return HTML tags.
     */
    public static String markdownToHtml(String content) {
        Parser markdownParser = new MarkdownParser();
        return markdownParser.parse(content);
    }
}
