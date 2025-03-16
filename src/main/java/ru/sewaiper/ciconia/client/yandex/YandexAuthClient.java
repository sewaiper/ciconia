package ru.sewaiper.ciconia.client.yandex;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import ru.sewaiper.ciconia.client.yandex.auth.IamTokenRequest;
import ru.sewaiper.ciconia.client.yandex.auth.IamTokenResponse;
import ru.sewaiper.ciconia.configuration.properties.YandexProperties;
import ru.sewaiper.ciconia.client.yandex.error.GenerateIamException;
import ru.sewaiper.ciconia.client.yandex.error.RSAKeyException;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Date;

public class YandexAuthClient extends AbstractYandexClient {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    private final YandexProperties properties;

    private String jwtToken;
    private String iamToken;

    public YandexAuthClient(YandexProperties properties) {

        this.properties = properties;

        this.publicKey = getPublicKey(properties.getKey().getPublicKey());
        this.privateKey = getPrivateKey(properties.getPrivateKey());
    }

    private RSAPublicKey getPublicKey(String value) {
        try {
            var factory = KeyFactory.getInstance("RSA");

            var reader = new PemReader(new StringReader(value));
            var spec = new X509EncodedKeySpec(reader.readPemObject().getContent());
            reader.close();

            return (RSAPublicKey) factory.generatePublic(spec);
        } catch (Exception e) {
            throw new RSAKeyException("Unable to get public key", e);
        }
    }

    private RSAPrivateKey getPrivateKey(String value) {
        try {
            var factory = KeyFactory.getInstance("RSA");

            var reader = new PemReader(new StringReader(value));
            var spec = new PKCS8EncodedKeySpec(reader.readPemObject().getContent());
            reader.close();

            return (RSAPrivateKey) factory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RSAKeyException("Unable to get private key", e);
        }
    }

    public synchronized @NonNull String getIamToken() {
        if (StringUtils.isBlank(this.iamToken)) {
            this.iamToken = generateIam();
        }

        try {
            validateJwt();
        } catch (ExpiredJwtException e) {
            this.iamToken = generateIam();
        }

        return String.format("Bearer %s", this.iamToken);
    }

    private void validateJwt() {
        Jwts.parser().verifyWith(publicKey)
                .build()
                .parse(jwtToken);
    }

    private String generateIam() {
        this.jwtToken = generateJwt();

        var entity = getRestClient().method(HttpMethod.POST)
                .uri("https://iam.api.cloud.yandex.net/iam/v1/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new IamTokenRequest(this.jwtToken))
                .retrieve()
                .toEntity(IamTokenResponse.class);

        if (entity.getBody() == null) {
            throw new GenerateIamException("IAM token response body is missing");
        }

        return entity.getBody().iamToken();
    }

    private String generateJwt() {
        var now = Instant.now();

        return Jwts.builder()
                .header()
                .add("kid", properties.getKey().getId())
                .and()
                .issuer(properties.getKey().getServiceAccountId())
                .audience().add("https://iam.api.cloud.yandex.net/iam/v1/tokens").and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(privateKey, Jwts.SIG.PS256)
                .compact();
    }
}
