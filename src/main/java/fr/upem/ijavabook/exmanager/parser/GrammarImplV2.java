package fr.upem.ijavabook.exmanager.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

/**
 * Represent Markdown grammar.
 * @author SIVANANTHAM Steeve - steeve.sivanantham@gmail.com
 */
@BuildParseTree
class GrammarImplV2 extends BaseParser<Object> {//DEV

    Rule Text(){
        return OneOrMore(CharRange(' ','~' ));
    }


    Rule OneCharFrame(char c1, char c2, Rule r){
        return Sequence(Ch(c1),
                r,
                Ch(c2));
    }

    Rule Lines(){
        return Sequence(Line(),
                EOI);
    }

    Rule Line(){
        return FirstOf(Html(),
                Markdown());
    }

    Rule Html(){
        return FirstOf(OneCharFrame('<','>',
                Sequence(Text(),
                        Ch('/'))),
                Sequence(OneCharFrame('<','>',Text()),
                        Text(),
                        OneCharFrame('<','>',
                                Sequence(Ch('/'),Text())))
                );
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
                DashHeader("###"),
                DashHeader("####"),
                DashHeader("#####"),
                DashHeader("######"));
    }

    Rule DashHeader(String dashs){
        return Sequence(String(dashs),
                Text());
    }

    Rule Header1(){
        return FirstOf(DashHeader("#"),
                SublineHeader('+'));
    }

    Rule Header2(){
        return FirstOf(DashHeader("##"),
                SublineHeader('-'));
    }

    Rule SublineHeader(char c){
        return Sequence(Text(),
                Ch('\n'),
                OneOrMore(Ch(c)),
                Ch('\n'));
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
        return FirstOf(SymbolList('*'),
                SymbolList('+'),
                SymbolList('-'),
                NumberList());
    }

    Rule SymbolList (char c){
        return OneOrMore(Sequence(Ch(c),
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

    Rule Links(){
        return FirstOf(Sequence(
                OneCharFrame('[', ']', Text()),
                OneCharFrame('(', ')',
                        Sequence(Text(),
                                OneCharFrame('"', '"', Text())))),
                Sequence(OneCharFrame('[', ']', Text()),
                        OneCharFrame('(', ')', Text())));
    }


    Rule Emphasis(){
        return FirstOf(StrongEmphasis(),
                SimpleEmphasis());
    }

    Rule StrongEmphasis(){
        return FirstOf(OneCharFrame('*', '*',
                OneCharFrame('*','*',Text())),
                OneCharFrame('_', '_',
                        OneCharFrame('_','_',Text())));
    }

    Rule SimpleEmphasis(){
        return FirstOf(OneCharFrame('*','*',Text()),
                        OneCharFrame('_','_',Text()));
    }

    Rule Code(){
        return OneCharFrame('\'','\'',Text());
    }

    Rule Images(){
        return Sequence(Ch('!'),
                OneCharFrame('[',']', Text()),
                OneOrMore('(',')',Text()));
    }
}
