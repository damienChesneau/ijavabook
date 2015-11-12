package fr.upem.ijavabook.exmanager.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.support.Var;

/**
 * Represent Markdown grammar.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
@BuildParseTree
class GrammarImpl extends BaseParser<Object> {//DEV

    public Rule Lines() {
        Var<MarkdownItem> tasks = new Var<>(new MarkdownItem());
        return Sequence(OneOrMore(Line(tasks)), EOI, push(tasks.getAndClear()));
    }

    public Rule Line(final Var<MarkdownItem> tasks) {
        Var<PictureTag.Builder> dto = new Var<>(new PictureTag.Builder());
        return Sequence(PictureSymbole(dto), Optional(Options(dto)),
                Optional(Newline()), push(tasks.get().addPicture(dto.get()))).label(
                "task");
    }

    public Rule PictureSymbole(final Var<PictureTag.Builder> dto) {
        return Sequence(PicturePrefix(), ArrayPrefix(), AltData(dto), ArraySuffix(),
                ParenPrefix(), SrcVal(dto), ImgTitle(dto), ParenSuffix(), push(""));
    }

    public Rule ImgTitle(final Var<PictureTag.Builder> dto) {
        return Sequence(String('\"'), StringVal(), push(dto.get().setTitle(match())), Ch('\"'));
    }

    public Rule AltData(final Var<PictureTag.Builder> dto) {
        return Sequence(StringVal()
                , push(dto.get().setAltText(match().trim())));
    }

    public Rule SrcVal(final Var<PictureTag.Builder> dto) {
        return Sequence(PathVal(), push(dto.get().setSrc(match().trim())));
    }

    public Rule PathVal() {
        return Sequence(ZeroOrMore((Ch('/')), StringVal()), TestNot(Ch(' ')));
    }

    public Rule StringVal() {
        return ZeroOrMore(Optional(CharRange('A', 'Z')),
                CharRange('a', 'z'),
                Optional(Ch('.')),
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

    public Rule Options(final Var<PictureTag.Builder> dto) {
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
