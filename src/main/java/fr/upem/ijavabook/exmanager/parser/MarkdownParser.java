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
                parser.Tasks()).run(content);
        MarkdownItem markdownItem = result.resultValue;
        List<Img> imgs = markdownItem.tasks();
        System.out.println(imgs);
        return null;
    }
}
