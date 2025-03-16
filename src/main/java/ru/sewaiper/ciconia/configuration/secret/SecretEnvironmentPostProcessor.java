package ru.sewaiper.ciconia.configuration.secret;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class SecretEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final Set<String> allowedExtensions;
    private final YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();

    public SecretEnvironmentPostProcessor() {
        this.allowedExtensions = Set.of(yamlLoader.getFileExtensions());
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {

        var secretRoot = environment.getProperty("secret.root");

        if (StringUtils.isBlank(secretRoot)) {
            return;
        }

        var root = Path.of(secretRoot);

        try(var files = Files.walk(root)) {
            files.forEach(path -> addProperties(path, environment));
        } catch (IOException e) {
            throw new SecretFileException("Unable to process secret files", e);
        }
    }

    private void addProperties(Path path,
                               ConfigurableEnvironment environment) {

        var resource = new FileSystemResource(path);
        var extension = FilenameUtils.getExtension(resource.getFilename());

        if (!allowedExtensions.contains(extension)) {
            return;
        }

        processYaml(resource, environment);
    }

    private void processYaml(Resource resource,
                             ConfigurableEnvironment environment) {

        var sources = environment.getPropertySources();

        try {
            yamlLoader.load(resource.getFilename(), resource)
                    .forEach(sources::addLast);
        } catch (IOException e) {
            throw new SecretFileException(
                    "Unable to process YAML file " + resource.getFilename(), e
            );
        }
    }
}
