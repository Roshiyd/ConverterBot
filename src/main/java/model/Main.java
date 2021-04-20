package model;

import com.sun.javafx.css.converters.CursorConverter;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main  {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi api=new TelegramBotsApi();
        try {
            api.registerBot(new Converter());
            System.out.println("Bot ishga tushdi");
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

    }
}