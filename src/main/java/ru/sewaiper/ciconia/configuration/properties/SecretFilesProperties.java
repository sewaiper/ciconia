package ru.sewaiper.ciconia.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@SuppressWarnings("unused")
@ConfigurationProperties(prefix = "secret")
public class SecretFilesProperties {

    private Path root;

    public Path getRoot() {
        return root;
    }

    public void setRoot(Path root) {
        this.root = root;
    }
}
