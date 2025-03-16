package ru.sewaiper.ciconia.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@TestConfiguration
public class TestExecutorsConfiguration {

    @Bean
    public TaskExecutor packetHandleExecutor() {
        return new SyncTaskExecutor();
    }

    @Bean
    public TaskExecutor publishHandleExecutor() {
        return new SyncTaskExecutor();
    }
}
