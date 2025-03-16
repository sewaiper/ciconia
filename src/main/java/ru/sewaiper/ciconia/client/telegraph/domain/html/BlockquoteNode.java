package ru.sewaiper.ciconia.client.telegraph.domain.html;

public class BlockquoteNode extends HtmlNode {

    public BlockquoteNode(String text) {
        setTag("blockquote").addChild(new PNode(text));
    }
}
