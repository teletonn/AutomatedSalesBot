import info.blockchain.api.receive.ReceiveResponse;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Messages {

    private String address = "";
    private int index;
    private long chatId;
    private String id;
    private String callbackUrl = System.getenv("callbackUrl");    //Heroku Var
    private boolean isConfirmed;
    private String trxHash;
    private BigDecimal value;
    private String currency;
    private BigDecimal expectedBalance;
    private long actualBalance;
    private String lastMessage = "";

    Exchanger exchanger = new Exchanger();
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    Receiver receiver = new Receiver();
    Xpub xPub = new Xpub();
    BlockExplorerImpl blockExplorerImpl = new BlockExplorerImpl();
    StatisticsImpl statisticsImpl = new StatisticsImpl();
    SendChatAction sendTypeAction = new SendChatAction();
    SendChatAction sendUploadAction = new SendChatAction();
    SendDocument sendDocument = new SendDocument();

    ArrayList<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
    KeyboardRow firstKeyboardRow = new KeyboardRow();
    KeyboardRow secondKeyboardRow = new KeyboardRow();
    KeyboardRow thirdKeyboardRow = new KeyboardRow();

    public void createKeyboard(ReplyKeyboardMarkup keyboardMarkup){

        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
    }
    public String startMessage(ReplyKeyboardMarkup keyboardMarkup){
        keyboard.clear();
        firstKeyboardRow.clear();
        secondKeyboardRow.clear();
        firstKeyboardRow.add("Buy source code");
        secondKeyboardRow.add("Exchanger");
        secondKeyboardRow.add("Blockchain Checker");
        secondKeyboardRow.add("Features");
        keyboard.add(firstKeyboardRow);
        keyboard.add(secondKeyboardRow);
        keyboardMarkup.setKeyboard(keyboard);
    return "What do you want to do?";
    }

    public String buySourceCode(ReplyKeyboardMarkup keyboardMarkup){
        keyboard.clear();
        firstKeyboardRow.clear();
        secondKeyboardRow.clear();
        firstKeyboardRow.add("Info about");
        firstKeyboardRow.add("Start preparing transaction");
        secondKeyboardRow.add("Check trx status");
        secondKeyboardRow.add("Menu");
        keyboard.add(firstKeyboardRow);
        keyboard.add(secondKeyboardRow);
        keyboardMarkup.setKeyboard(keyboard);

        return "What's next?";
    }

    public String infoAbout(ReplyKeyboardMarkup keyboardMarkup){
        return System.getenv("AboutLink");
    }

    public String preparinTransaction(ReplyKeyboardMarkup keyboardMarkup){
        System.out.println("Start preparing transaction");

        try {
            System.out.println(xPub.getxPub());
            receiver.checkXpubGap(xPub.getxPub());
            ReceiveResponse response = receiver.receive(xPub.getxPub(), callbackUrl);
            receiver.setActualPriceInBtc();
            address = response.getReceivingAddress();
            index = response.getIndex();
        } catch (Exception e) {
            System.out.println("Exception when getting response");
            if (e.getMessage().contains("Problem with xpub")) {
                xPub.getNewXpub(xPub.xpubListCreator());
                System.out.println("New xPub is: " + xPub.getxPub());
                try {
                    ReceiveResponse response = receiver.receive(xPub.getxPub(), callbackUrl);
                    address = response.getReceivingAddress();
                    index = response.getIndex();
                } catch (Exception e1) {
                    if (e1.getMessage().contains("Problem with xpub")) {
                        System.out.println("You need new Xpubs");
                    }
                    return "Please contact " + System.getenv("ownerName");    //Heroku Var
                }
            }
            e.printStackTrace();
        }

        keyboard.clear();
        firstKeyboardRow.clear();
        secondKeyboardRow.clear();
        thirdKeyboardRow.clear();
        firstKeyboardRow.add("Check trx status");
        secondKeyboardRow.add("Cancel trx");
        thirdKeyboardRow.add("Menu");
        keyboard.add(firstKeyboardRow);
        keyboard.add(secondKeyboardRow);
        keyboard.add(thirdKeyboardRow);
        keyboardMarkup.setKeyboard(keyboard);

        return "Send " + "*" + receiver.getAmountInBtc() + "* to "
                + "\n"
                + address
                + "\n"
                + "Payment should be in *ONE* transaction";
    }

   public String exchangeMessage(ReplyKeyboardMarkup keyboardMarkup){
       System.out.println("Exchanger wanted");

       keyboard.clear();
       firstKeyboardRow.clear();
       secondKeyboardRow.clear();
       thirdKeyboardRow.clear();
       firstKeyboardRow.add("\uD83C\uDDFA\uD83C\uDDF8USD to BTC");
       firstKeyboardRow.add("\uD83C\uDDEA\uD83C\uDDFAEUR to BTC");
       firstKeyboardRow.add("\uD83C\uDDF5\uD83C\uDDF1PLN to BTC");
       secondKeyboardRow.add("\uD83D\uDCB0 BTC to USD");
       thirdKeyboardRow.add("Menu");
       keyboard.add(firstKeyboardRow);
       keyboard.add(secondKeyboardRow);
       keyboard.add(thirdKeyboardRow);
       keyboardMarkup.setKeyboard(keyboard);

       return "Choose your currency";
   }

   public String blockChainCheker(ReplyKeyboardMarkup keyboardMarkup){
       keyboard.clear();
       firstKeyboardRow.clear();
       secondKeyboardRow.clear();
       thirdKeyboardRow.clear();
       firstKeyboardRow.add("Check TRX by Hash");
       firstKeyboardRow.add("Check Address Balance");
       secondKeyboardRow.add("Fork checker");
       secondKeyboardRow.add("Today's blocks quantity");
       thirdKeyboardRow.add("Menu");
       keyboard.add(firstKeyboardRow);
       keyboard.add(secondKeyboardRow);
       keyboard.add(thirdKeyboardRow);
       keyboardMarkup.setKeyboard(keyboard);

       return "What would you like to check?";
   }

   public String forkChecker(ReplyKeyboardMarkup keyboardMarkup){
       keyboard.clear();
       firstKeyboardRow.clear();
       secondKeyboardRow.clear();
       thirdKeyboardRow.clear();
       firstKeyboardRow.add("Check chain for fork");
       secondKeyboardRow.add("Menu");
       keyboard.add(firstKeyboardRow);
       keyboard.add(secondKeyboardRow);
       keyboardMarkup.setKeyboard(keyboard);

       return "*How it work's:* " +
               "\n" +
               "`Get's the latest block on the main chain and read its height`" +
               "\n" +
               "`Use the previous block height to get a list of blocks at that height`" +
               "\n" +
               "`and detect a potential chain fork`" +
               "\n" +
               "\n" +
               "*Press \"Check chain for fork\" and*" +
               "\n" +
               "*Wait a bit please, getting blocks takes a while*";
   }

   public String features(ReplyKeyboardMarkup keyboardMarkup){
       keyboard.clear();
       firstKeyboardRow.clear();
       secondKeyboardRow.clear();
       thirdKeyboardRow.clear();
       firstKeyboardRow.add("BTC market price in USD");
       firstKeyboardRow.add("Hash rate");
       secondKeyboardRow.add("Number Of Transactions");
       thirdKeyboardRow.add("Menu");
       keyboard.add(firstKeyboardRow);
       keyboard.add(secondKeyboardRow);
       keyboard.add(thirdKeyboardRow);
       keyboardMarkup.setKeyboard(keyboard);

       return "Features still in progress";
   }
}
