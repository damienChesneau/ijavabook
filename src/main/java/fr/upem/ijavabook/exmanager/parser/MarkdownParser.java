package fr.upem.ijavabook.exmanager.parser;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import java.util.List;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class MarkdownParser implements Parser {

    @Override
    public String parse(String content) {
        System.out.println(content);
        GrammarImpl parser = Parboiled.createParser(GrammarImpl.class);
        ParsingResult<MarkdownItem> result = new RecoveringParseRunner<MarkdownItem>(
                parser.Lines()).run(content);
        MarkdownItem markdownItem = result.resultValue;
        List<HtmlProducer> pictureTags = markdownItem.getAll();
        System.out.println(pictureTags);
        String html = pictureTags.stream().map(htmlProducer -> htmlProducer.produce()).reduce("", (a, b) -> a + b);
        return html;
    }
}
