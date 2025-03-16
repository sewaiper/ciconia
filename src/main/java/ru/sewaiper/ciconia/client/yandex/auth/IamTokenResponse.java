package ru.sewaiper.ciconia.client.yandex.auth;

import java.time.Instant;

public record IamTokenResponse(String iamToken,
                               Instant expiresAt) {
}
