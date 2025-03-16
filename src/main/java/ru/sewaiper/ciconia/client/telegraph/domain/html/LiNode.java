package ru.sewaiper.ciconia.client.telegraph.domain.html;

@SuppressWarnings("unused")
public class LiNode extends HtmlNode {

    public LiNode(Node element) {
        setTag("li").addChild(element);
    }
}
