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
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private long chatId;
    private String id;
    private String userName;
    private String adminUserName = System.getenv("ownerName");    //Heroku Var
    private int userCount;


    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    SendChatAction sendTypeAction = new SendChatAction();
    SendChatAction sendUploadAction = new SendChatAction();
    SendDocument sendDocument = new SendDocument();
    Messages messages = new Messages();
    AdminsMessages adminsMessages = new AdminsMessages();
    List<String> usersList = new ArrayList<>();
    {
        usersList.add(System.getenv("ownerName"));
    }

    public void onUpdateReceived(Update update) {
        update.getUpdateId();
        userName = getUserName(update);
        usersListAdder();
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
            if (msg.equals("Admin Menu")){
                return adminsMessages.adminMenu(keyboardMarkup);
            }
            if (msg.equals("Check Bot Users")){
                return adminsMessages.checkBotUsers(keyboardMarkup);
            }
            if (msg.equals("Check quantity")){
                userCounter();
                return "Users quantity: *" + userCount + "*";
            }
            if (msg.equals("Check Users List")){
                return userListPrinter();
            }

            if (msg.equals("Check what users checked")){
                return adminsMessages.usersChecked(keyboardMarkup);
            }
            if (msg.equals("Users Transactions")){
                return adminsMessages.usersTrx(keyboardMarkup);
            }
            if (msg.equals("Check TRX quantity")){
                return "Checked transactions quantity: *" + elementsInListCounter(messages.usersTransactionsList) + "*";
            }
            if (msg.equals("Check TRX List")){
                return listPrinter(messages.usersTransactionsList);
            }
            if (msg.equals("Users Addresses")){
                return adminsMessages.usersAddresses(keyboardMarkup);
            }
            if (msg.equals("Check Addresses quantity")){
                return "Checked addresses quantity: *" + elementsInListCounter(messages.usersAddressesList) + "*";
            }
            if (msg.equals("Check Addresses List")){
                return listPrinter(messages.usersAddressesList);
            }

            if (msg.equals("Check your balance")){
                return adminsMessages.checkAdminBalance();
            }
            if(msg.equals("Admin Features")){
                return "In progress";
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

    public String getUserName(Update update){
        if(update.getMessage().getChat().getUserName().equals("") || update.getMessage().getChat().getUserName().equals(null)){
            return update.getMessage().getChat().getFirstName() +
                    " " +
                    update.getMessage().getChat().getLastName();
        }
        else {
            return "@" + update.getMessage().getChat().getUserName();
        }
    }

    public void usersListAdder(){
        if(!usersList.contains(userName)){
            usersList.add(userName);
        }
    }

    public String userListPrinter(){
        String allUsers = usersList.get(0);
        for (int i = 1; i < usersList.size(); i++){
            allUsers += "\n" + usersList.get(i);
        }
        return "All users list: \n" + allUsers;
    }

    public void userCounter(){
        userCount = usersList.size();
    }

    public String listPrinter(List list){
        String fullList = "Full list: \n";
        for(int i = 0; i < list.size(); i++){
            fullList += list.get(i) + "\n";
        }
        return fullList;
    }

    public int elementsInListCounter(List list){
        int listCounter = list.size();
        return listCounter;
    }


}