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

    Rule Text(){
        return OneOrMore(CharRange(' ','~' ));
    }

    Rule Lines(){
        return Sequence(Line(),
                EOI);
    }

    Rule Line(){
        return FirstOf(Markdown(),
                Html());
    }

    Rule Html(){
        return Text();
    }

    Rule Markdown(){
        return FirstOf(Bloc(),
                Inline());
    }

    Rule Bloc(){
        return FirstOf(Sequence(ElemBlock(),
                ZeroOrMore(Bloc())),
                Line());
    }

    Rule ElemBlock(){
        return FirstOf(Headers(),
                Blockquotes(),
                Lists(),
                CodeBlock(),
                Horizontale());
    }

    Rule Inline(){
        return FirstOf(Links(),
                Emphasis(),
                Code(),
                Images());
    }

    Rule Headers(){
        return FirstOf(Header1(),
                Header2(),
                dashHeader("###"),
                dashHeader("####"),
                dashHeader("#####"),
                dashHeader("######"));
    }

    Rule dashHeader(String dashs){
        return Sequence(String(dashs),
                Text());
    }

    Rule Header1(){
        return FirstOf(dashHeader("#"),
                Sequence(Text(),
                        Ch('\n'),
                        OneOrMore(Ch('=')),
                        Ch('\n')));
    }

    Rule Header2(){
        return FirstOf(dashHeader("##"),
                Sequence(Text(),
                        Ch('\n'),
                        OneOrMore(Ch('-')),
                        Ch('\n')));
    }

    Rule Blockquotes(){
        return OneOrMore(Sequence(Ch('>'),
                FirstBlockquotes()));
    }

    Rule FirstBlockquotes(){
        return FirstOf(Sequence(Ch('>'),Line()),
                Line());
    }

    Rule Lists(){
        return FirstOf(StarList(),
                PlusList(),
                MinusList(),
                NumberList());
    }

    Rule StarList(){
        return OneOrMore(Sequence(Ch('*'),
                Line()));
    }

    Rule PlusList(){
        return OneOrMore(Sequence(Ch('+'),
                Line()));
    }

    Rule MinusList(){
        return OneOrMore(Sequence(Ch('-'),
                Line()));
    }

    Rule NumberList(){
        return OneOrMore(Sequence(OneOrMore(CharRange('0','9')),
                Ch('.'),
                Line()));
    }

    Rule CodeBlock(){
        return OneOrMore(Sequence(Ch('\t'),
                Line()));
    }

    Rule Horizontale(){
        return FirstOf(OneOrMore(Ch('*')),
                OneOrMore(Ch('-')),
                OneOrMore(Ch('*')));
    }
}
