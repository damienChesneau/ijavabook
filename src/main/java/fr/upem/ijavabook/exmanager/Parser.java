package fr.upem.ijavabook.exmanager;

import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parse a markdown string to a html string.
 * This class is thread safe.
 *
 * @author Steeve Sivanantham
 */
class Parser {

    private int nextTagId = 0;
    private final Object monitor = new Object();
    private final Path rootDirectory;

    /**
     * Enum with all compiled pattern.
     */
    private enum ParserPattern {
        OPEN_JUNIT_PATTERN("<\\s*junit\\s*>"),
        CLOSE_JUNIT_PATTERN("<\\s*/\\s*junit\\s*>"),
        FILE_JUNIT_PATTERN("<\\s*junitFile\\s+src\\s*=\\s*\".*\"\\s*/\\s*>"),
        SRC_PATTERN("\".*\""),
        NB_PATTERN("\\d+");

        private final Pattern pattern;

        ParserPattern(String s) {
            this.pattern = Pattern.compile(s);
        }
    }

    /**
     * Parse a markdown string to a html string.
     *
     * @param lines markdown string
     * @return html string
     */
    public String parseMarkdown(String lines) throws IOException {
        return new PegDownProcessor().markdownToHtml(replaceFileJunit(replaceEmbedJunit(lines)));
    }

    Parser(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
    }

    private int getTagId() {
        synchronized (monitor) {
            return nextTagId++;
        }
    }

    private String replaceEmbedJunit(String html) {
        Matcher startMatch = ParserPattern.OPEN_JUNIT_PATTERN.pattern.matcher(html);
        while (startMatch.find()) {
            String startT = startTag();
            html = startMatch.replaceFirst(startT);
            processEndTag(html, startT);
            startMatch = ParserPattern.OPEN_JUNIT_PATTERN.pattern.matcher(html);
        }
        return html;
    }

    private String processEndTag(String html, String startT) {
        Matcher endMatch = ParserPattern.CLOSE_JUNIT_PATTERN.pattern.matcher(html);
        if (endMatch.find()) {
            html = endMatch.replaceFirst(endTag(startT));
        }
        return html;
    }

    private String replaceFileJunit(String html) throws IOException {
        Matcher tag = ParserPattern.FILE_JUNIT_PATTERN.pattern.matcher(html);
        while (tag.find()) {
            Optional<String> test = getSrc(tag.group());
            String startT = startTag();
            html = tag.replaceFirst(startT + (test.isPresent() ? test.get() : "") + endTag(startT));
            tag = ParserPattern.FILE_JUNIT_PATTERN.pattern.matcher(html);
        }
        return html;
    }

    private Optional<String> getSrc(String matchedTag) throws IOException {
        Matcher src = ParserPattern.SRC_PATTERN.pattern.matcher(matchedTag);
        if (src.find()) {
            String matchedSrc = src.group();
            return Optional.of(getLinesFromFinedFile(matchedSrc.substring(1, matchedSrc.length() - 1)));
        }
        return Optional.empty();
    }

    private String getLinesFromFinedFile(String matchedSrc) throws IOException {
        Path file = rootDirectory.resolve(matchedSrc);
        return Files.lines(file).collect(Collectors.joining("\n"));
    }

    private String startTag() {
        int id = getTagId();
        return "<div class = \"junitTest\"><pre id = \"junitPre" + id + "\"><code id=\"junitTest" + id + "\">";
    }

    private String endTag(String startTag) {
        Optional<String> id = getTagId(startTag);
        return "</code></pre><button type=\"button\"class=\"btn btn-primary\" onclick=\"sendJavaTest(\\$('#junitTest"
                + (id.isPresent() ? id.get() : "") + "'),\\$('#junitPre" + (id.isPresent() ? id.get() : "") + "'))\">Test</button></div>";
    }

    private Optional<String> getTagId(String startTag) {
        Matcher matcher = ParserPattern.NB_PATTERN.pattern.matcher(startTag);
        if (matcher.find()) {
            return Optional.of(matcher.group());
        }
        return Optional.empty();
    }


}
