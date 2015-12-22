package fr.upem.ijavabook.exmanager;

import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by steeve on 22/12/15.
 */
public class Parsers {

    private static final Pattern openJunitPattern = Pattern.compile("<\\s*/\\s*junit\\s*>");
    private static final Pattern closeJunitPattern = Pattern.compile("<\\s*junit\\s*>");
    private static final Pattern fileJunitPattern = Pattern.compile("<\\s*junitFile\\s+src\\s*=\\s*\".*\"\\s*/\\s*>");
    private static final Pattern srcPattern = Pattern.compile("\".*\"");


    public static String parseMarkdown(String lines){
        return new PegDownProcessor().markdownToHtml(replaceFileJunit(replaceEmbedJunit(lines)));
    }

    private static String replaceEmbedJunit(String html){
        return openJunitPattern.matcher(
                closeJunitPattern.matcher(html).replaceAll("<div class = \"junitTest\"><code>"))
                .replaceAll("</code></div>");
    }

    private static String replaceFileJunit(String html){
        Matcher balise = fileJunitPattern.matcher(html);
        while(balise.find()) {
            String matchedBalise = balise.group();
            Matcher src = srcPattern.matcher(matchedBalise);
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
            html = balise.replaceFirst("<div class = \"junitTest\"><code>" + test + "</code></div>");
            balise = fileJunitPattern.matcher(html);
        }
        return html;
    }
}
