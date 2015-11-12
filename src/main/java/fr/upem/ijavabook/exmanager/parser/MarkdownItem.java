package fr.upem.ijavabook.exmanager.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Store all matched objects in markdown grammar.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class MarkdownItem {//DEV
    private final ArrayList<HtmlProducer> tags = new ArrayList<>();

    MarkdownItem addPicture(final PictureTag.Builder imgBuilder) {
        tags.add(imgBuilder.build());
        return this;
    }

    List<HtmlProducer> getAll() {
        return (List<HtmlProducer>) tags.clone();
    }

    @Override
    public String toString() {
        return tags.stream().map(a -> a.produce()).collect(Collectors.joining("")).toString();
    }
}
