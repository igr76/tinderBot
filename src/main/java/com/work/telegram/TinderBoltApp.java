package com.work.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "j7666bot"; //TODO: добавь имя бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = "7399956767:AAHVAfzBjCCXwXK5e-6uT_l5_v4JoRpSWSE"; //TODO: добавь токен бота в кавычках
    public static final String OPEN_AI_TOKEN = "gpt:FUj5RRFaxyJPx8r99jGlJFkblB3TiwFfStVTSk3zPf89Bss9"; //TODO: добавь токен ChatGPT в кавычках


   private ArrayList<String>  list = new ArrayList<>();
   private  UserInfo me;
   private int questionCount;
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
        //command gpt
        if (message.equals("/gpt")) {
            currentMode= DialogMode.GPT;
            sendPhotoMessage("gpt");
            String text = loadMessage("gpt");
            sendTextMessage(text);
            return;
        }
        if (currentMode== DialogMode.GPT) {
            String promt = loadPrompt("gpt");
            Message msg = sendTextMessage("Подождите chatGPT думает");
            String answer = chatGPTService.sendMessage(promt,message);
            updateTextMessage(msg,answer);
            return;
        }
        //command date
        if (message.equals("/date")) {
            currentMode= DialogMode.DATE;
            sendPhotoMessage("date");
            String text = loadMessage("date");
            sendTextMessage(text);
            sendTextButtonsMessage("Выберите девушку для свидания",
                    "Ариана Гранде","date_grande",
                    "Марго Робби","date_robbie",
                    "Зендея","date_zendaya",
                    "Райан Гослинг","date_gosling",
                    "Том Харди","date_hardy"
            );
            return;
        }
        if (currentMode== DialogMode.DATE) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("date")) {
                sendPhotoMessage(query);
                sendTextMessage("отличный выбор! \n Твоя задача пригласить девушку" +
                        "за 5 сообщений");
                String promt = loadPrompt(query);
                Message msg = sendTextMessage("Подождите chatGPT думает");
                String answer = chatGPTService.addMessage(promt);
                updateTextMessage(msg,answer);
                return;
            }
        }
            //command message
        if (message.equals("/message")) {
                currentMode= DialogMode.MESSAGE;
                sendPhotoMessage("message");
                String text = loadMessage("message");
                sendTextMessage(text);
                sendTextButtonsMessage("Выберите девушку для свидания",
                        "Cледующее сообщение","next",
                        "Пригласить на свидание","message_date"
                );
                return;
        }
        if (currentMode== DialogMode.MESSAGE) {
                String query = getCallbackQueryButtonKey();
                if (query.startsWith("message")) {
                    sendPhotoMessage(query);
                   String userChatHistory = String.join("\n\n",list);
                    String promt = loadPrompt(query);
                   Message msg = sendTextMessage("Подождите chatGPT думает");
                   String answer = chatGPTService.sendMessage(promt,userChatHistory);
                    updateTextMessage(msg,answer);
                }
            list.add(message);
            return;
        }
        //command profile
        if (message.equals("/profile")) {
            currentMode= DialogMode.PROFILE;
            sendPhotoMessage("profile");
            me= new UserInfo();
            questionCount=1;
            sendTextMessage("Сколько вам лет?");
            return;
        }
        if (currentMode== DialogMode.PROFILE) {
            switch (questionCount) {
                case 1:me.age=message;
                    questionCount=2;
                    sendTextMessage("Кем вы работаете?");
                    return;
                case 2:me.occupation=message; questionCount=3;
                        sendTextMessage("Какое у вас хобби?");
                        return;
                case 3:me.hobby=message;questionCount=4;
                    sendTextMessage("Что вам не нравиться в людях?");
                    return;
                case 4:me.annoys=message;questionCount=5;
                    sendTextMessage("Цель знакомства?");
                    return;
                case 5:me.goals=message;
                    String promt = loadPrompt("profile");
                    Message msg = sendTextMessage("Подождите chatGPT думает");
                    String answer = chatGPTService.sendMessage(promt,message);
                    updateTextMessage(msg,answer);
                    return;
            }
            return;


        }
        //command orener
        if (message.equals("/opener")) {
            currentMode= DialogMode.OPENER;
            sendPhotoMessage("opener");
            String text = loadMessage("opener");
            me= new UserInfo();
            questionCount=1;
            sendTextMessage("Сколько ей лет?");
            return;
        }
        if (currentMode== DialogMode.OPENER) {
            switch (questionCount) {
                case 1:me.age=message;
                    questionCount=2;
                    sendTextMessage("Кем она работает?");
                    return;
                case 2:me.occupation=message; questionCount=3;
                    sendTextMessage("Есть ли у неё  хобби? и какие");
                    return;
                case 3:me.hobby=message;questionCount=4;
                    sendTextMessage("Что ей не нравиться в людях?");
                    return;
                case 4:me.annoys=message;questionCount=5;
                    sendTextMessage("Цель знакомства?");
                    return;
                case 5:me.goals=message;
            String aboutFriend= message;
            String promt = loadPrompt("opener");
                Message msg = sendTextMessage("Подождите chatGPT думает");
                String answer = chatGPTService.sendMessage(promt,aboutFriend);
                updateTextMessage(msg,answer);

            return;}
        }



    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
