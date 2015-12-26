package fr.upem.ijavabook.exmanager;

import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parse a markdown string to a html string.
 * This class is thread safe.
 * @author Steeve Sivanantham
 */
public class Parser {

    private int nextId = 0;
    private final Object monitor = new Object();

    /**
     * Enum with all compiled pattern.
     */
    enum ParserPattern{
        openJunitPattern("<\\s*junit\\s*>"),
        closeJunitPattern("<\\s*/\\s*junit\\s*>"),
        fileJunitPattern("<\\s*junitFile\\s+src\\s*=\\s*\".*\"\\s*/\\s*>"),
        srcPattern("\".*\""),
        nbPattern("\\d+");

        private final Pattern pattern;
        ParserPattern(String s) {
            this.pattern = Pattern.compile(s);
        }
    }

    /**
     * Parse a markdown string to a html string.
     * @param lines markdown string
     * @return html string
     */
    public String parseMarkdown(String lines){
        return new PegDownProcessor().markdownToHtml(replaceFileJunit(replaceEmbedJunit(lines)));
    }

    private int getId(){
        synchronized (monitor){
            return nextId ++;
        }
    }

    private String replaceEmbedJunit(String html){
        Matcher startMatch = ParserPattern.openJunitPattern.pattern.matcher(html);
        while (startMatch.find()){
            String startT = startTag();
            html = startMatch.replaceFirst(startT);
            Matcher endMatch = ParserPattern.closeJunitPattern.pattern.matcher(html);
            if(endMatch.find()){
                html = endMatch.replaceFirst(endTag(startT));
            }
            startMatch = ParserPattern.openJunitPattern.pattern.matcher(html);
        }
        return html;
    }

    private String replaceFileJunit(String html){
        Matcher Tag = ParserPattern.fileJunitPattern.pattern.matcher(html);
        while(Tag.find()) {
            String matchedTag = Tag.group();
            Matcher src = ParserPattern.srcPattern.pattern.matcher(matchedTag);
            String test = "";
            if(src.find()) {
                String matchedSrc = src.group();
                try {
                    test = Files.lines(Paths.get("markdown/"+matchedSrc.substring(1, matchedSrc.length() - 1))).collect(Collectors.joining("\n"));
                    //We must remove "markdown/"+ for de prod !
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String startT= startTag();
            html = Tag.replaceFirst(startT + test + endTag(startT));
            Tag = ParserPattern.fileJunitPattern.pattern.matcher(html);
        }
        return html;
    }

    private String startTag() {
        int id = getId();
        return "<div class = \"junitTest\"><pre id = \"junitPre"+id+"\"><code id=\"junitTest"+id+"\">";
    }
    private String endTag(String startTag) {
        Matcher matcher = ParserPattern.nbPattern.pattern.matcher(startTag);
        String id = "";
        if(matcher.find()){
            id = matcher.group();
        }
        return "</code></pre><button type=\"button\"class=\"btn btn-primary\" onclick=\"sendJavaTest(\\$('#junitTest"+id+"'),\\$('#junitPre"+id+"'))\">Test</button></div>";
    }


}
