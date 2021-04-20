package model;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Converter extends TelegramLongPollingBot {

    public static final String botToken = "1280008895:AAEOk8wdendpURX6vOqx_TTgBi8Wvns-kSM";
    public static final String botName = "curconuzbot";
    int level = 0;
    Currency selected = null;
    String ccy = null;


    public String getBotUsername() {
        return botName;
    }

    public String getBotToken() {
        return botToken;
    }

    @SneakyThrows
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        Message message = update.getMessage();
        String chatText = message.getText();
        Long chatId = message.getChatId();
        sendMessage.setChatId(chatId);
        LocalDateTime localDateTime=LocalDateTime.now();
        String date=localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String time=localDateTime.format(DateTimeFormatter.ofPattern("hh:mm:ss"));
        if (chatText.equals("/start")) {
            sendMessage.setText("Bizni botga xush kelibsiz " + message.getFrom().getFirstName() + "!");
            level = 0;
        }

        switch (level) {
            case 0:
                mainButtons(sendMessage);
                level = 1;
                break;
            case 1:
                if (chatText.equalsIgnoreCase("⬅")) {
                    sendMessage.setText("⬅");
                    mainButtons(sendMessage);
                } else if (chatText.equalsIgnoreCase("\uD83C\uDF10 Kurs info")) {
                    sendMessage.setText("ℹ");
                    infoButton(sendMessage);
                    level = 2;
                } else if (chatText.equalsIgnoreCase("\uD83D\uDD04 Konvertor")) {
                    sendMessage.setText("Quyidagilardan birini tanlang ⬇");
                    converterButtons(sendMessage);
                    level = 3;
                }
                break;
            case 2:
                if (chatText.equalsIgnoreCase("⬅")) {
                    sendMessage.setText("⬅");
                    mainButtons(sendMessage);
                    level = 1;
                } else {
                    sendMessage.setText("Valyuta haqida ma'lumotlar \uD83D\uDCDD");
                    List<Currency> currencyArrayList = ConverterUtil.getCurrencyList();
                    for (Currency currency : currencyArrayList) {
                        if (currency.getCcy().equalsIgnoreCase(message.getText().substring(0, 3))) {
                            selected = currency;
                            break;
                        }
                    }
                    infoCurrency(sendMessage);
                    level = 6;
                }
                break;
            case 3:
                if (chatText.equalsIgnoreCase("⬅")) {
                    sendMessage.setText("⬅");
                    mainButtons(sendMessage);
                    level = 1;
                } else if (chatText.equalsIgnoreCase("So'mga almashtirish ➡\uD83C\uDDFA\uD83C\uDDFF")) {
                    sendMessage.setText("So'mga almashtirish");
                    toSumConverter(sendMessage);
                    level = 4;
                } else if (chatText.equalsIgnoreCase("Valyuta olish \uD83C\uDDFA\uD83C\uDDFF➡")) {
                    sendMessage.setText("So'mni kiriting:"+"\n(❗️1000 so'mdan yuqori bo'lgan qiymatni kiriting)");
                    sumToConverter(sendMessage);
                    level = 7;
                }

                break;
            case 4:
                if (chatText.equalsIgnoreCase("⬅")) {
                    sendMessage.setText("⬅");
                    converterButtons(sendMessage);
                    level = 3;
                } else if (chatText.equalsIgnoreCase("\uD83C\uDDFA\uD83C\uDDF8USD\uD83D\uDD04\uD83C\uDDFA\uD83C\uDDFFUZS")) {
                    ccy = message.getText().substring(4, 7);
                    sendMessage.setText("Ayirboshlash summasini kiriting ⬇");
                    level = 5;
                }else if (chatText.equalsIgnoreCase("\uD83C\uDDEA\uD83C\uDDFAEUR\uD83D\uDD04\uD83C\uDDFA\uD83C\uDDFFUZS")) {
                    ccy = message.getText().substring(4, 7);
                    sendMessage.setText("Ayirboshlash summasini kiriting ⬇");
                    level = 5;
                } else if (chatText.equalsIgnoreCase("\uD83C\uDDF7\uD83C\uDDFARUB\uD83D\uDD04\uD83C\uDDFA\uD83C\uDDFFUZS")) {
                    ccy = message.getText().substring(4, 7);
                    sendMessage.setText("Ayirboshlash summasini kiriting ⬇");
                    level = 5;
                }
                break;
            case 5:
                if (chatText.equalsIgnoreCase("⬅")) {
                    sendMessage.setText("⬅");
                    toSumConverter(sendMessage);
                    level = 4;
                }else  {
                    Double value = Double.valueOf(message.getText());
                    for (Currency currency : ConverterUtil.getCurrencyList()) {
                        if (ccy.equalsIgnoreCase(currency.getCcy())) {
                            double rate = Double.parseDouble(String.valueOf(currency.getRate()));
                            long sum=(long)(value*rate);
                            StringBuilder stringBuilder=new StringBuilder();
                            stringBuilder.append(" "+value+" "+currency.getCcyNm_UZ())
                                    .append("\n⏩ "+(sum)).append(" so'm")
                                    .append("\n")
                                    .append("\n"+"\uD83D\uDDD3Sana: "+date)
                                    .append("\n"+"\uD83D\uDD58Vaqt: "+time);
                            sendMessage.setText(String.valueOf(stringBuilder));
                        }
                    }
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setOneTimeKeyboard(true);
                    replyKeyboardMarkup.setSelective(true);

                    List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
                    KeyboardRow keyboardRow = new KeyboardRow();
                    KeyboardButton back = new KeyboardButton("⬅");
                    keyboardRow.add(back);
                    rows.add(keyboardRow);
                    replyKeyboardMarkup.setKeyboard(rows);
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                }
            break;

            case 6:
                if (chatText.equalsIgnoreCase("⬅")) {
                    sendMessage.setText("⬅");
                    infoButton(sendMessage);
                    level = 2;
                }
                break;
            case 7:
                if (chatText.equalsIgnoreCase("⬅")) {
                    sendMessage.setText("⬅");
                    converterButtons(sendMessage);
                    level = 3;
                }else {
                    double value= Double.parseDouble(chatText);
                    long longValue=(long)value;
                    List<Currency> list=ConverterUtil.getCurrencyList();
                    StringBuilder stringBuilder=new StringBuilder();
                    stringBuilder.append(longValue +" so'm  ⏩")
                            .append("\n\uD83C\uDDFA\uD83C\uDDF8"+(value/list.get(0).getRate())+" "+list.get(0).getCcyNm_UZ())
                            .append("\n\uD83C\uDDEA\uD83C\uDDFA"+(value/list.get(1).getRate())+" "+list.get(1).getCcyNm_UZ())
                            .append("\n\uD83C\uDDF7\uD83C\uDDFA"+(value/list.get(2).getRate())+" "+list.get(2).getCcyNm_UZ())
                            .append("\n")
                            .append("\n"+"\uD83D\uDDD3Sana: "+date)
                            .append("\n"+"\uD83D\uDD58Vaqt: "+time);
                    sendMessage.setText(String.valueOf(stringBuilder));
                }
                break;


        }
        execute(sendMessage);
    }

    public void mainButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton infoButton = new KeyboardButton("\uD83C\uDF10 Kurs Info");
        KeyboardButton converterButton = new KeyboardButton("\uD83D\uDD04 Konvertor");

        keyboardRow.add(infoButton);
        keyboardRow.add(converterButton);

        rows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public void infoButton(SendMessage sendMessage) throws IOException {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        KeyboardRow backRow = new KeyboardRow();
        KeyboardButton backButton = new KeyboardButton();

        backButton.setText("⬅");
        backRow.add(backButton);
        rows.add(backRow);
        replyKeyboardMarkup.setKeyboard(rows);
        List<Currency> list = ConverterUtil.getCurrencyList();
        for (Currency currency : list) {
            KeyboardRow keyboardRow = new KeyboardRow();
            KeyboardButton ccyButton = new KeyboardButton();
            ccyButton.setText(currency.getCcy() + " ↔ " + currency.getCcyNm_UZ());
            keyboardRow.add(ccyButton);
            rows.add(keyboardRow);
        }

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

    }

    public void converterButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardButton toSum = new KeyboardButton("So'mga almashtirish ➡\uD83C\uDDFA\uD83C\uDDFF");
        KeyboardButton fromSum = new KeyboardButton("Valyuta olish \uD83C\uDDFA\uD83C\uDDFF➡");
        KeyboardButton back = new KeyboardButton("⬅");

        keyboardRow.add(toSum);
        keyboardRow.add(fromSum);
        keyboardRow1.add(back);

        rows.add(keyboardRow);
        rows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public void infoCurrency(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton backButton = new KeyboardButton("⬅");

        keyboardRow.add(backButton);
        rows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Valyuta turi \uD83D\uDCB8: " + selected.getCcy())
                .append("\n")
                .append("Valyuta nomi\uD83C\uDDFA\uD83C\uDDFF: " + selected.getCcyNm_UZ())
                .append("\n")
                .append("Valyuta kursi \uD83D\uDCB1: " + selected.getRate() + " so'm")
                .append("\n")
                .append("Valyuta o'zgargan sana \uD83D\uDDD3: " + selected.getDate());

        sendMessage.setText(String.valueOf(stringBuilder));
    }

    public void toSumConverter(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();
        KeyboardButton fromDollar = new KeyboardButton("\uD83C\uDDFA\uD83C\uDDF8USD\uD83D\uDD04\uD83C\uDDFA\uD83C\uDDFFUZS");
        KeyboardButton fromEURO = new KeyboardButton("\uD83C\uDDEA\uD83C\uDDFAEUR\uD83D\uDD04\uD83C\uDDFA\uD83C\uDDFFUZS");
        KeyboardButton fromRUBL = new KeyboardButton("\uD83C\uDDF7\uD83C\uDDFARUB\uD83D\uDD04\uD83C\uDDFA\uD83C\uDDFFUZS");
        KeyboardButton back = new KeyboardButton("⬅");

        keyboardRow.add(fromDollar);
        keyboardRow1.add(fromEURO);
        keyboardRow2.add(fromRUBL);
        keyboardRow3.add(back);

        rows.add(keyboardRow);
        rows.add(keyboardRow1);
        rows.add(keyboardRow2);
        rows.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public void sumToConverter(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("⬅"));
        rows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

}
