package fr.upem.ijavabook.exmanager.parser;

import java.util.Objects;

/**
 * Im-mutable class represent a picture and can traduce his content to HTML.
 * This class has a private constructor. Use his builder to construct it.
 * @see PictureTag.Builder
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class PictureTag implements HtmlProducer {
    private final String altText;
    private final String src;
    private final String title;

    private PictureTag(String altText, String src, String title) {
        this.altText = Objects.requireNonNull(altText);
        this.src = Objects.requireNonNull(src);
        this.title = Objects.requireNonNull(title);
    }

    public final String getAltText() {
        return altText;
    }

    public final String getSrc() {
        return src;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Image [getContent=" + altText + ", getHref=" + src + ", title=" + title + "]";
    }

    @Override
    public String produce() {
        String html = "<p>";
        html += "<img getHref=\"";
        html += src;
        html += "\" alt=\"";
        html += altText;
        html += "\" title=\"" + title + "\"" + " /></p>";
        return html;
    }

    static class Builder {
        private String altText;
        private String src;
        private String title;

        Builder setAltText(final String altText) {
            this.altText = altText;
            return this;
        }

        Builder setTitle(final String title) {
            this.title = title;
            return this;
        }

        Builder setSrc(final String src) {
            this.src = src;
            return this;
        }

        PictureTag build() {
            return new PictureTag(altText, src, title);
        }
    }
}
