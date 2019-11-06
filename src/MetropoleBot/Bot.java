package MetropoleBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

class Bot
{
	private HashMap<Long, String> commands = new HashMap<>();
	private TextArea log;

	Bot(TextArea log)
	{
		this.log = log;
	}

	void start()
	{
		// Create your bot passing the token received from @BotFather
		TelegramBot bot = new TelegramBot("1068564256:AAGsb9uiUmM37Vpnkrl7cQPKjVWrgKI-2NQ");

		// Register for updates
		bot.setUpdatesListener(updates -> {
			// Process updates
			updates.forEach(update -> {
				long chatId = update.message().chat().id();
				String msg = update.message().text();
				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();

				// Saving commands
				commands.put(chatId, msg);

				// Send message
				SendResponse response = bot.execute(new SendMessage(chatId, "Fala Desgraça!"));

				// Log
				System.out.println("\n" + timeFormatter.format(now) + " - chatID: " + chatId + " - msg: " + msg);
				log.appendText("\n" + timeFormatter.format(now) + " - chatID: " + chatId + " - msg: " + msg);
			});
			// Return id of last processed update or confirm them all
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}
}