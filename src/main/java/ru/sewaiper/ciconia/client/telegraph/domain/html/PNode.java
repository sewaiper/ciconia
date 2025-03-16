package ru.sewaiper.ciconia.client.telegraph.domain.html;

public class PNode extends HtmlNode {

    public PNode(String text) {
        setTag("p").addChild(TextNode.of(text));
    }
}
