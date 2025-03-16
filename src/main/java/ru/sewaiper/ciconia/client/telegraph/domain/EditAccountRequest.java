package ru.sewaiper.ciconia.client.telegraph.domain;

public record EditAccountRequest(String access_token,
                                 String short_name,
                                 String author_name,
                                 String author_url) {
}
