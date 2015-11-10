package fr.upem.ijavabook.exmanager;

import fr.upem.ijavabook.exmanager.parser.CompleteDslParser;
import fr.upem.ijavabook.exmanager.parser.Task;
import fr.upem.ijavabook.exmanager.parser.TaskList;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
public class ExerciseImpl implements ExerciceService {
    @Override
    public String getExercise(String name) {
        return getExercise(Paths.get(name));
    }

    @Override
    public String getExercise(Path file) {
        String line1 = "![Alt text]\n";

        String dslString = line1;//+ "\n" + line2 + "\n" + line3;
        CompleteDslParser parser = Parboiled.createParser(CompleteDslParser.class);
        ParsingResult<TaskList> result = new RecoveringParseRunner<TaskList>(
                parser.Tasks()).run(dslString);
        TaskList taskList = result.resultValue;
        List<Task> tasks = taskList.tasks();
        return null;
    }
}
