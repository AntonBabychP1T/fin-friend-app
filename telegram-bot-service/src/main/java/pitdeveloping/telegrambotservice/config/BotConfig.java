package pitdeveloping.telegrambotservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pitdeveloping.telegrambotservice.bot.FinTrackerBot;

@Configuration
public class BotConfig {
    private final FinTrackerBot finTrackerBot;

    public BotConfig(FinTrackerBot telegramBot) {
        this.finTrackerBot = telegramBot;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(finTrackerBot);
            return telegramBotsApi;
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't create Telegram bot API", e);
        }
    }
}
