package fr.upem.ijavabook.exmanager;

import fr.upem.ijavabook.exmanager.parser.CompleteDslParser;
import fr.upem.ijavabook.exmanager.parser.Task;
import fr.upem.ijavabook.exmanager.parser.TaskList;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CompleteDslParserTest {


    @Test
    public void shouldParseMultipleLines() throws Exception {
        CompleteDslParser parser = Parboiled.createParser(CompleteDslParser.class);
        String line1 = "![Alt text]\n";

        String dslString = line1 ;//+ "\n" + line2 + "\n" + line3;
        ParsingResult<TaskList> result = new RecoveringParseRunner<TaskList>(
                parser.Tasks()).run(dslString);
        TaskList taskList = result.resultValue;
        List<Task> tasks = taskList.tasks();
        assertThat(tasks.size(), equalTo(1));

        Task task1 = tasks.get(0);
        System.out.println("sum="+task1.summary());
        System.out.println("ass="+task1.assignee());
        System.out.println("lab="+task1.labels().toString());
    }
}
