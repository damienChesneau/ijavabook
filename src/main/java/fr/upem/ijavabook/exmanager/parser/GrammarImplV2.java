package fr.upem.ijavabook.exmanager.parser;

import com.sun.org.apache.xpath.internal.axes.NodeSequence;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.support.Var;

/**
 * Represent Markdown grammar.
 * @author SIVANANTHAM Steeve - steeve.sivanantham@gmail.com
 */
@BuildParseTree
class GrammarImplV2 extends BaseParser<Object> {//DEV
        Rule lines(){
            return Sequence(line(),
                    EOI);
        }

        Rule line(){
            return FirstOf(markdown(),
                    html());
        }

        Rule html(){
            return OneOrMore(CharRange((char)0,
                    (char)255));
        }

        Rule markdown(){
            return FirstOf(bloc(),
                    inline());
        }

        Rule bloc(){
            return FirstOf(Sequence(elemBlock(),
                    ZeroOrMore(bloc())),
                    line());
        }

         Rule elemBlock(){
            return FirstOf(headers(),
                    blockquotes(),
                    lists(),
                    codeBlock(),
                    horizontale());
        }

        Rule inline(){
            return FirstOf(links(),
                    emphasis(),
                    code(),
                    images());
        }

        Rule headers(){
            return FirstOf(header1(),
                    header2(),
                    dashHeader("###"),
                    dashHeader("####"),
                    dashHeader("#####"),
                    dashHeader("######"));
        }

        Rule dashHeader(String dashs){
            return Sequence(String(dashs),
                    OneOrMore(CharRange((char)0,
                            (char)255)));
        }

}
