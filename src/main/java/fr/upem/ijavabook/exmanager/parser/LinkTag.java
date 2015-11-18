package fr.upem.ijavabook.exmanager.parser;

import java.util.Objects;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class LinkTag implements HtmlProducer {
    private final String content;
    private final String href;
    private final String title;

    private LinkTag(String content, String href, String title) {
        this.content = Objects.requireNonNull(content);
        this.href = Objects.requireNonNull(href);
        this.title = Objects.requireNonNull(title);
    }

    @Override
    public String toString() {
        return "Link [getContent=" + content + ", getHref=" + href + ", title=" + title + "]";
    }

    @Override
    public String produce() {
        String html = "<a href=\"";
        html += href;
        html += "\" title=\"";
        html += title;
        html += "\">" + content + "</p>";
        return html;
    }

    static class Builder {
        private String content;
        private String href;
        private String title;

        Builder setContent(final String content) {
            this.content = content;
            return this;
        }

        Builder setHref(final String href) {
            this.href = href;
            return this;
        }

        Builder setTitle(final String title) {
            this.title = title;
            return this;
        }

        LinkTag build() {
            return new LinkTag(content, href, title);
        }
    }
}
