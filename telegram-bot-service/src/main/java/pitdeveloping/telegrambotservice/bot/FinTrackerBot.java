package pitdeveloping.telegrambotservice.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Component
public class FinTrackerBot extends TelegramLongPollingBot {
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            String fileId = update.getMessage().getPhoto().get(0).getFileId();
            try {
                File file = execute(new GetFile(fileId));
                String filePath = file.getFilePath();
                java.io.File downloadedFile = downloadFile(filePath);

                // Збереження файлу у спільній директорії
                Path sharedDir = Paths.get("/shared");
                if (!Files.exists(sharedDir)) {
                    Files.createDirectories(sharedDir);
                }
                Path targetPath = sharedDir.resolve(downloadedFile.getName());
                Files.copy(downloadedFile.toPath(), targetPath);

                // Відправка шляху до файлу в Kafka
                kafkaTemplate.send("telegram-bot-topic", targetPath.toString());
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Привіт! Надішліть фото чеку для обробки.");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
