package fr.upem.ijavabook.exmanager.parser;

import org.junit.Test;
import org.parboiled.Parboiled;

public class GrammarImplTest {


    @Test
    public void shouldParseMultipleLines() throws Exception {
        GrammarImpl parser = Parboiled.createParser(GrammarImpl.class);
//        String line1 = "![Update this]\n";
        String s = Parsers.markdownToHtml("![Update this](/path/img.jpg \"Title\")\n");
        System.out.println(s);

//        String line1 = "![Update this](/path/img.jpg \"Title\")\n";
//        System.out.println("Ask ="+line1);
//        String dslString = line1 ;//+ "\n" + line2 + "\n" + line3;
//        ParsingResult<MarkdownItem> result = new RecoveringParseRunner<MarkdownItem>(
//                parser.Tasks()).run(dslString);
//        MarkdownItem markdownItem = result.resultValue;
//        List<Img> imgs = markdownItem.tasks();
//        assertThat(imgs.size(), equalTo(1));
//
//        Img img1 = imgs.get(0);
//        System.out.println("sum="+ img1.altText());
//        System.out.println("ass="+ img1.assignee());
//        System.out.println("lab="+ img1.labels().toString());
    }
}
