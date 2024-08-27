package com.work.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "j7666bot"; //TODO: добавь имя бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = "7399956767:AAHVAfzBjCCXwXK5e-6uT_l5_v4JoRpSWSE"; //TODO: добавь токен бота в кавычках
    public static final String OPEN_AI_TOKEN = "gpt:FUj5RRFaxyJPx8r99jGlJFkblB3TiwFfStVTSk3zPf89Bss9"; //TODO: добавь токен ChatGPT в кавычках

    private ChatGPTService chatGPTService;
    private DialogMode currentMode = null;
    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        //TODO: основной функционал бота будем писать здесь
        String message = update.getMessage().getText();
        if (message.equals("/start")) {
            currentMode= DialogMode.MAIN;
            sendPhotoMessage("main");
            String text = loadMessage("main");
            sendTextMessage(text);
            showMainMenu("главное меню бота","/start",
                    "генерация Tinder-профиля","/profile",
                    "сообщение для знакомства 🥰","/opener",
                    "переписка от вашего имени 😈","/message",
                    "переписка со звездами 🔥","/date",
                    "Общение с ИИ","/gpt",
                    "Демонстрация html","/html");
            return;
        }
        if (message.equals("/gpt")) {
            currentMode= DialogMode.GPT;
            sendPhotoMessage("gpt");
            String text = loadMessage("gpt");
            sendTextMessage(text);
            return;
        }
        if (currentMode== DialogMode.GPT) {
            String promt = loadPrompt("gpt");
            String answer=chatGPTService.sendMessage(promt,message);
            sendTextMessage(answer);
            return;
        }

        sendTextButtonsMessage("выберите режим работы",
                "старт","start","стоп","stop");
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
