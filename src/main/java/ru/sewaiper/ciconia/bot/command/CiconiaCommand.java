package ru.sewaiper.ciconia.bot.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sewaiper.ciconia.bot.CiconiaMessageTo;

import java.util.Optional;

public interface CiconiaCommand {

    CommandType command();

    String getDescription();

    Optional<CiconiaMessageTo> apply(Message message);

    Optional<CiconiaMessageTo> execute(Message message);
}
