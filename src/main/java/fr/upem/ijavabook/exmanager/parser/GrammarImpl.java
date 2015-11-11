package fr.upem.ijavabook.exmanager.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.support.Var;

@BuildParseTree
class GrammarImpl extends BaseParser<Object> {//DEV

    public Rule Tasks() {
        Var<MarkdownItem> tasks = new Var<>(new MarkdownItem());
        return Sequence(OneOrMore(Task(tasks)), EOI, push(tasks.getAndClear()));
    }

    public Rule Task(final Var<MarkdownItem> tasks) {
        Var<Img> dto = new Var<>(new Img());
        return Sequence(PictureSymb(dto), Optional(Options(dto)),
                Optional(Newline()), push(tasks.get().add(dto.get()))).label(
                "task");
    }

    public Rule PictureSymb(final Var<Img> dto) {
        return Sequence(PicturePrefix(), ArrayPrefix(), AltData(dto), ArraySuffix(),
                ParenPrefix(), SrcVal(dto), push(""));
    }

    public Rule AltData(final Var<Img> dto) {
        return Sequence(StringVal()
                , push(dto.get().altText(match())));
    }

    public Rule SrcVal(final Var<Img> dto) {
        return Sequence(PathVal(), push(dto.get().setSrc(match())));
    }

    public Rule PathVal() {
        return Sequence(Optional(Ch('/')), ZeroOrMore(StringVal(), Ch('/'), Optional(Ch('/'))));
    }


    public Rule StringVal() {
        return ZeroOrMore(Optional(CharRange('A', 'Z')),
                CharRange('a', 'z'),
                Optional(CharRange('A', 'Z')),
                Optional(Ch(' ')),
                Optional(CharRange('A', 'Z')), Optional(TestNot(Ch(']'))));
    }

    public Rule Chars() {
        return OneOrMore(TestNot(OptLim()), ANY);
    }

    public Rule Test() {
        return String("]");
    }

    public Rule Options(final Var<Img> dto) {
        return Sequence(OptSep(),
                Optional(""));
    }

    public Rule OptSep() {
        return Sequence(OptSp(), OptLim(), OptSp());
    }


    public Rule PicturePrefix() {
        return Ch('!');
    }

    public Rule OptLim() {
        return Ch('s');
    }

    public Rule FieldSep() {
        return Ch(':');
    }

    public Rule OptSp() {
        return ZeroOrMore(' ');
    }

    public Rule ValSep() {
        return Ch(',');
    }

    public Rule ParenPrefix() {
        return Ch('(');
    }

    public Rule ParenSuffix() {
        return Ch('(');
    }

    public Rule ArrayPrefix() {
        return Ch('[');
    }

    public Rule ArraySuffix() {
        return Ch(']');
    }


    public Rule Newline() {
        return FirstOf('\n', Sequence('\r', Optional('\n')));
    }
}
