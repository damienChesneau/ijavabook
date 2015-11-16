package fr.upem.ijavabook.exmanager.parser;

import fr.upem.ijavabook.exmanager.Exercises;
import org.junit.Test;
import org.parboiled.Parboiled;

import java.nio.file.Paths;

public class GrammarImplTest {


    @Test
    public void shouldParseMultipleLines() throws Exception {
        GrammarImpl parser = Parboiled.createParser(GrammarImpl.class);
        String line1 = "![Update this]\n";
        Exercises.getExerciseSrv().getExercise(Paths.get("markdown/file.text"));
        String s = Parsers.markdownToHtml("![Update this](/path/img.jpg \"Title\")\n");
        System.out.println(s);

    }
}
