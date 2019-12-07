import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.net.URL;

public class Bot extends TelegramLongPollingBot {

    private long chatId;
    private String id;
    private String userName;
    private String adminUserName = System.getenv("ownerName");    //Heroku Var


    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    SendChatAction sendTypeAction = new SendChatAction();
    SendChatAction sendUploadAction = new SendChatAction();
    SendDocument sendDocument = new SendDocument();
    Messages messages = new Messages();
    AdminsMessages adminsMessages = new AdminsMessages();


    public void onUpdateReceived(Update update) {
        update.getUpdateId();

        messages.xPub.xPubInit();

        sendTypeAction.setChatId(update.getMessage().getChatId());
        sendTypeAction.setAction(ActionType.TYPING);
        sendUploadAction.setAction(ActionType.UPLOADDOCUMENT);

        try {
            URL url = new URL(System.getenv("FileUrl"));    //Heroku Var
            String fileName = System.getenv("FileName");    //Heroku Var
            InputStream stream = url.openStream();
            sendDocument.setChatId(update.getMessage().getChatId()).setDocument(fileName, stream);

        } catch (Exception e) {
            e.printStackTrace();
        }

        SendMessage sendMessage = new SendMessage().enableMarkdown(true).setChatId(update.getMessage().getChatId());
        chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        sendMessage.setReplyMarkup(keyboardMarkup);

        id = sendMessage.setChatId(update.getMessage().getChatId()).getChatId();
        userName = "@" + update.getMessage().getChat().getUserName();

        try {
            sendMessage.setText(getMessage(text));
            execute(sendTypeAction);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public String getBotUsername() {
        return System.getenv("BotName");    //Heroku Var
    }

    public String getBotToken() {
        return System.getenv("BotToken");    //Heroku Var

    }


    public String getMessage(String msg) {

        messages.createKeyboard(keyboardMarkup);

        if (msg.equals("/start") || msg.equals("Menu") || msg.equals("Hello")) {
            if(isAdmin(userName)){
                return adminsMessages.startAdminMessage(keyboardMarkup);
            }else {
                return messages.startMessage(keyboardMarkup);
            }
        }

        if(isAdmin(userName)){
            if(msg.contains("Admin Menu")){
                return adminsMessages.adminMenu(keyboardMarkup);
            }
        }

        if (msg.equals("Buy source code")) {
            return messages.buySourceCode(keyboardMarkup);
        }

        if (msg.equals("Info about")) {
            return messages.infoAbout(keyboardMarkup);
        }

        if (msg.equals("Start preparing transaction")) {
            return messages.preparingTransaction(keyboardMarkup);
        }

        if (msg.equals("Check trx status")) {
           return messages.checkTrxStatus();
        }

        if(messages.getLastMessage().contains("Check trx status")){
            sendFiles(messages.getIsPayed());
        }

        if (msg.equals("Cancel trx")) {
            return messages.cancelTrx();
        }

        if (msg.equals("Exchanger")) {
            return messages.exchangeMessage(keyboardMarkup);
        }

        if (msg.contains("USD to BTC")) {
            return messages.usdToBtc(msg);
        }

        if (messages.getLastMessage().contains("USD to BTC")) {
           return messages.exchangeUsdToBtc(msg);
        }

        if (msg.contains("EUR to BTC")) {
            return messages.eurToBtc(msg);
        }

        if (messages.getLastMessage().contains("EUR to BTC")) {
            return messages.exchangeEurToBtc(msg);
        }

        if (msg.contains("PLN to BTC")) {
            return messages.plnToBtc(msg);
        }

        if (messages.getLastMessage().contains("PLN to BTC")) {
           return messages.exchangePlnToBtc(msg);
        }

        if (msg.equals("Blockchain Checker")) {
            return messages.blockChainChecker(keyboardMarkup);
        }

        if (msg.equals("Check TRX by Hash")) {
            return messages.checkTrxByHash();
        }

        if (messages.getLastMessage().contains("Check TRX by Hash")) {
           return messages.checkingTrxByHash(msg);
        }

        if (msg.equals("Check Address Balance")) {
            return messages.checkAddressBalance();
        }

        if (messages.getLastMessage().contains("Check Address Balance")) {
            return messages.checkingAddressBalance(msg);
        }

        if (msg.equals("Fork checker")) {
            return messages.forkChecker(keyboardMarkup);
        }

        if (msg.contains("Check chain for fork")) {
            return messages.checkingFork();
        }

        if (msg.equals("Today's blocks quantity")) {
           return messages.todayBlocks();
        }

        if (msg.equals("Features")) {
            return messages.features(keyboardMarkup);
        }

        if (msg.equals("BTC market price in USD")) {
            return messages.btcMarketPrice();
        }

        if (msg.equals("Hash rate")) {
           return messages.hashRate();
        }

        if (msg.equals("Number Of Transactions")) {
            return messages.numberOfTrx();
        }

        return "How can I help you?";

    }

    public String sendFiles(Boolean isPayed){
        if(isPayed) {
            try {
                execute(sendUploadAction.setChatId(id));
                execute(sendDocument.setChatId(id));
                messages.setLastMessage("");
                return "This .zip with your source code and instructions";
            } catch (TelegramApiException e) {
                e.printStackTrace();
                 return "Server error, dont worry and tell about this situation to " + System.getenv("ownerName");    //Heroku Var
            }
        }
        else {
            return "You didnt Pay yet";
        }
    }

    public boolean isAdmin(String userName){
        if (userName.equals(adminUserName)){
            return true;
        }
        else {
            return false;
        }
    }

}