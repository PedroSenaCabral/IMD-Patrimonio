package lpii.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class Main{

    public static void main(String[] args){
// Create your bot passing the token received from @BotFather
        TelegramBot bot = new TelegramBot("1068564256:AAGsb9uiUmM37Vpnkrl7cQPKjVWrgKI-2NQ");

// Register for updates
        bot.setUpdatesListener(updates -> {
            // Send messages
            updates.forEach(update -> {
                        long chatId = update.message().chat().id();
                        SendResponse response = bot.execute(new SendMessage(chatId, "Fala Desgraça!"));
                        System.out.println(chatId);
                        System.out.println(update.message().text());
                    });
            // ... process updates
            // return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}