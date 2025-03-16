package ru.sewaiper.ciconia.client.telegraph.domain.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class HtmlNode implements Node {

    private String tag;
    private Map<String, String> attrs;
    private List<Node> children;

    public String getTag() {
        return tag;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public List<Node> getChildren() {
        return children;
    }

    public HtmlNode setTag(String tag) {
        this.tag = tag;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public HtmlNode addAttr(String key, String value) {
        if (attrs == null) {
            attrs = new HashMap<>();
        }
        attrs.put(key, value);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public HtmlNode addChild(Node child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        return this;
    }
}
