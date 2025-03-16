package ru.sewaiper.ciconia.fake;

import org.apache.commons.lang3.RandomStringUtils;
import ru.sewaiper.ciconia.client.telegraph.domain.response.Page;
import ru.sewaiper.ciconia.client.telegraph.domain.response.Response;

public final class TelegraphFaker {

    private TelegraphFaker() {}

    public static Response<Page> page() {
        var response = new Response<Page>();
        response.setOk(true);
        response.setResult(pageData());
        return response;
    }

    public static Page pageData() {
        var path = RandomStringUtils.insecure().nextAlphabetic(10);

        return new Page(
                path,
                String.format("https://telegra.ph/%s", path),
                path
        );
    }
}
