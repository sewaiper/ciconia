package ru.sewaiper.ciconia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.sewaiper.ciconia.bot.CiconiaBot;
import ru.sewaiper.ciconia.bot.CiconiaBotRegistry;
import ru.sewaiper.ciconia.bot.command.CommandType;
import ru.sewaiper.ciconia.client.telegraph.TelegraphClient;
import ru.sewaiper.ciconia.configuration.TestExecutorsConfiguration;
import ru.sewaiper.ciconia.db.QueryLogger;
import ru.sewaiper.ciconia.client.tdlib.NativeClientWrapper;
import ru.sewaiper.ciconia.client.tdlib.TdlibClient;
import ru.sewaiper.ciconia.client.yandex.YandexDomainClient;
import ru.sewaiper.ciconia.fake.UserFaker;
import ru.sewaiper.ciconia.model.Channel;
import ru.sewaiper.ciconia.model.Message;
import ru.sewaiper.ciconia.fake.ChannelFaker;
import ru.sewaiper.ciconia.fake.MessageFaker;
import ru.sewaiper.ciconia.model.User;
import ru.sewaiper.ciconia.repository.ChannelRepository;
import ru.sewaiper.ciconia.repository.MessageRepository;
import ru.sewaiper.ciconia.repository.UserRepository;
import ru.sewaiper.ciconia.repository.packet.PacketRepository;

import java.time.Duration;
import java.util.Properties;

@SuppressWarnings("unused")
@AutoConfigureMockMvc
@SpringBootTest(classes = {
        TelegramNewsApplication.class,
        QueryLogger.class,
        TestExecutorsConfiguration.class
})
@ContextConfiguration(initializers = BaseTest.Initializer.class)
public abstract class BaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NonNull ConfigurableApplicationContext context) {
            try {
                @SuppressWarnings("resource")
                var postgres = new PostgreSQLContainer<>("postgres:13.3");

                postgres.start();

                var properties = new Properties();
                properties.setProperty("spring.datasource.url", postgres.getJdbcUrl());
                properties.setProperty("spring.datasource.username", postgres.getUsername());
                properties.setProperty("spring.datasource.password", postgres.getPassword());

                var ps = new PropertiesPropertySource("sqliteForTest", properties);

                context.getEnvironment()
                        .getPropertySources()
                        .addLast(ps);

                Runtime.getRuntime().addShutdownHook(new Thread(postgres::stop));
            } catch (Exception e) {
                throw new IllegalStateException("Unable to initialize context", e);
            }
        }
    }

    // TDLib
    @MockitoBean
    protected TdlibClient tdlibClient;
    @MockitoBean
    protected NativeClientWrapper nativeClientWrapper;

    // Yandex
    @MockitoBean
    protected YandexDomainClient yandexClient;

    // Ciconia bot
    @MockitoBean
    protected CiconiaBotRegistry ciconiaBotRegistry;
    @MockitoBean
    protected CiconiaBot ciconiaBot;

    // Telegraph
    @MockitoBean
    protected TelegraphClient telegraphClient;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private PacketRepository packetRepository;

    public User createUser() {
        var entity = UserFaker.entity();

        userRepository.insert(
                entity.getId(), entity.getUsername(),
                entity.getFirstName(), entity.getLastName(),
                CommandType.START.name(), entity.getChatId()
        );

        return entity;
    }

    public void subscribe(long userId, long channelId) {
        channelRepository.subscribe(userId, channelId);
    }

    public void createPacketConfig(long userId, int maxSize) {
        packetRepository.updateConfig(userId, maxSize, (int) Duration.ofHours(1).toMillis());
    }

    public Channel createChannel() {
        var entity = ChannelFaker.entity();

        channelRepository.insert(
                entity.getId(), entity.getSupergroupId(),
                entity.getName(), entity.getTitle()
        );

        return entity;
    }

    public Message createMessage(long channelId) {
        var message = MessageFaker.entity(channelId);

        messageRepository.insert(
                message.getId(), message.getChannelId(),
                message.getLink(), message.getBrief(),
                message.getText(), message.getTag()
        );

        return message;
    }
}
