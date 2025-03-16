package ru.sewaiper.ciconia.client.telegraph.domain;

import ru.sewaiper.ciconia.client.telegraph.domain.html.HtmlNode;

import java.util.List;

public record CreatePageRequest(String access_token,
                                String title,
                                List<HtmlNode> content) {
}
