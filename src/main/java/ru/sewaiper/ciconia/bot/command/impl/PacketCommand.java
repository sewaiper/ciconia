package ru.sewaiper.ciconia.bot.command.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sewaiper.ciconia.bot.CiconiaMessageTo;
import ru.sewaiper.ciconia.bot.command.CiconiaCommand;
import ru.sewaiper.ciconia.bot.command.CommandType;
import ru.sewaiper.ciconia.bot.error.CiconiaException;
import ru.sewaiper.ciconia.service.message.packet.PacketService;
import ru.sewaiper.ciconia.service.user.UserService;

import java.util.Arrays;
import java.util.Optional;

@Component
public class PacketCommand implements CiconiaCommand {

    private final UserService userService;
    private final PacketService packetService;

    public PacketCommand(UserService userService,
                         @Lazy PacketService packetService) {
        this.userService = userService;
        this.packetService = packetService;
    }

    @Override
    public CommandType command() {
        return CommandType.PACKET;
    }

    @Override
    public String getDescription() {
        return "Настроить пакет";
    }

    @Override
    public Optional<CiconiaMessageTo> apply(Message message) {
        var userId = message.getFrom().getId();

        userService.setCommand(userId, command());

        return Optional.of(
                new CiconiaMessageTo(message.getChatId(), getHelpText())
        );
    }

    private String getHelpText() {
        return """
                Для настройки пакета отправь мне сообщение в формате: {maxSize},{ttl}
                1) {maxSize} - желаемое количество сообщений в пакете
                2) {ttl}     - время жизни пакета в формате ISO-8601:
                    PT20.345S - это 20.345 секунд
                    PT15M - это 15 минут
                    PT10H - это 10 часов
                
                Например, для настройки "10,PT5M", в течении 5 минут я буду собирать пакет из 10 сообщений, после\
                чего отправлю тебе ссылку на Telegraph статью с брифингом и классификацией собранных данных. В \
                случае, если за 5 минут не удалось набрать 10 сообщений, то я отправлю статью из данных, которые\
                удалось собрать к этому моменту.
                """;
    }

    @Override
    public Optional<CiconiaMessageTo> execute(Message message) {
        var userId = message.getFrom().getId();

        var config = parseConfig(message.getText());

        packetService.configurePacket(userId, config.maxSize, config.ttl);

        return Optional.empty();
    }

    private PacketConfig parseConfig(String text) {
        var candidates = Arrays.stream(StringUtils.split(text, ','))
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new);

        var maxSize = Optional.ofNullable(ArrayUtils.get(candidates, 0))
                .map(Integer::parseInt)
                .orElseThrow(() -> new CiconiaException("Message text invalid format"));

        var ttl = Optional.ofNullable(ArrayUtils.get(candidates, 1))
                .orElseThrow(() -> new CiconiaException("Message text invalid format"));

        return new PacketConfig(maxSize, ttl);
    }

    private record PacketConfig(int maxSize, String ttl) {
    }
}
