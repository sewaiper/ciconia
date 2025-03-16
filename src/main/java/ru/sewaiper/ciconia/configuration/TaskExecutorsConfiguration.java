package ru.sewaiper.ciconia.configuration;

import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;

import java.time.Duration;

@Configuration
@Profile("!test")
public class TaskExecutorsConfiguration {

    @Bean
    public TaskExecutor packetHandleExecutor() {
        return new ThreadPoolTaskExecutorBuilder()
                .threadNamePrefix("packet-handler-")
                .corePoolSize(1)
                .maxPoolSize(1)
                .keepAlive(Duration.ofSeconds(60))
                .allowCoreThreadTimeOut(true)
                .queueCapacity(1024)
                .build();
    }

    @Bean
    public TaskExecutor publishHandleExecutor() {
        return new ThreadPoolTaskExecutorBuilder()
                .threadNamePrefix("publish-handler-")
                .corePoolSize(4)
                .maxPoolSize(8)
                .keepAlive(Duration.ofSeconds(60))
                .allowCoreThreadTimeOut(true)
                .queueCapacity(1024)
                .build();
    }
}
