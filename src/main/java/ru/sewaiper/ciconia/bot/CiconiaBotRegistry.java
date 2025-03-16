package ru.sewaiper.ciconia.bot;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.sewaiper.ciconia.bot.error.CiconiaException;

@Component
public class CiconiaBotRegistry implements SmartInitializingSingleton {

    private final CiconiaBot bot;
    private final TelegramBotsApi api;

    public CiconiaBotRegistry(CiconiaBot bot) {
        try {
            this.api = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            throw new CiconiaException("Unable to create core Telegram Bot API registry", e);
        }

        this.bot = bot;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            api.registerBot(bot);
            bot.setCommands();
        } catch (TelegramApiException e) {
            throw new CiconiaException("Unable to register telegram bot", e);
        }
    }
}
