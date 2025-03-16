package ru.sewaiper.ciconia.client.telegraph.domain.html;

import java.util.Map;

public class HeadNode extends HtmlNode {

    private static final Map<Integer, String> HEAD_TAGS = Map.of(
            3, "h3", 4, "h4"
    );

    public HeadNode(int level, String text) {
        var tag = HEAD_TAGS.get(level);
        if (tag == null) {
            throw new IllegalArgumentException("Level " + level + " is not allowed");
        }

        setTag(tag).addChild(TextNode.of(text));
    }
}
