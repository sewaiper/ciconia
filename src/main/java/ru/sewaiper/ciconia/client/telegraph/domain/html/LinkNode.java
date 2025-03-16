package ru.sewaiper.ciconia.client.telegraph.domain.html;

import org.springframework.lang.NonNull;

public class LinkNode extends HtmlNode {

    public LinkNode(@NonNull String href, @NonNull String title) {
        setTag("a")
                .addAttr("href", href)
                .addChild(TextNode.of(title));
    }
}
