package ru.sewaiper.ciconia.client.telegraph.domain;

import ru.sewaiper.ciconia.client.telegraph.domain.html.HtmlNode;

import java.util.List;

@SuppressWarnings("unused")
public record EditPageRequest(String access_token,
                              String path,
                              String title,
                              List<HtmlNode> content) {
}
