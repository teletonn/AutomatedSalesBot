import info.blockchain.api.APIException;
import info.blockchain.api.receive.ReceiveResponse;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.Exchanger;

public class Bot extends TelegramLongPollingBot {

    private String address = "";
    private int index;
    private long chatId;
    private String id;
    private String callbackUrl = "https://google.com/";
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


    public void onUpdateReceived(Update update) {
        update.getUpdateId();

        xPubInit();

        sendTypeAction.setChatId(update.getMessage().getChatId());
        sendTypeAction.setAction(ActionType.TYPING);
        sendUploadAction.setAction(ActionType.UPLOADDOCUMENT);
        sendDocument.setChatId(update.getMessage().getChatId()).setDocument(new File(Bot.class.getResource("source.txt").getFile()));

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

    private void xPubInit() {
        if (xPub.getxPub().equals("")) {
            xPub.getNewXpub(xPub.xpubListCreator());
        }
    }

    public String getBotUsername() {
        return System.getenv("BotName");    //Heroku Var
    }

    public String getBotToken() {
        return System.getenv("BotToken");    //Heroku Var
    }


    public String getMessage(String msg) {
        ArrayList<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
        KeyboardRow firstKeyboardRow = new KeyboardRow();
        KeyboardRow secondKeyboardRow = new KeyboardRow();
        KeyboardRow thirdKeyboardRow = new KeyboardRow();

        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        if (msg.equals("/start") || msg.equals("Menu") || msg.equals("Hello")) {
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

        if (msg.equals("Buy source code")) {
            keyboard.clear();
            firstKeyboardRow.clear();
            secondKeyboardRow.clear();
            firstKeyboardRow.add("Info about");
            firstKeyboardRow.add("Start preparing transaction");
            secondKeyboardRow.add("Chek trx status");
            secondKeyboardRow.add("Menu");
            keyboard.add(firstKeyboardRow);
            keyboard.add(secondKeyboardRow);
            keyboardMarkup.setKeyboard(keyboard);

            return "What's next?";

        }

        if (msg.equals("Info about")) {
            System.out.println("Info about");
            return "Info about";
        }

        if (msg.equals("Start preparing transaction")) {
            System.out.println("Start preparing transaction");

            try {
                System.out.println(xPub.getxPub());
                receiver.checkXpubGap(xPub.getxPub());
                ReceiveResponse response = receiver.receive11(xPub.getxPub(), callbackUrl);
                receiver.setActualPriceInBtc();
                address = response.getReceivingAddress();
                index = response.getIndex();
            } catch (Exception e) {
                System.out.println("Exception when getting response");
                if (e.getMessage().contains("Problem with xpub")) {
                    xPub.getNewXpub(xPub.xpubListCreator());
                    System.out.println("New xPub is: " + xPub.getxPub());
                    try {
                        ReceiveResponse response = receiver.receive11(xPub.getxPub(), callbackUrl);
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

        if (msg.equals("Chek trx status")) {
            try {
                trxHash = blockExplorerImpl.getTrxHash(address);
                isConfirmed = blockExplorerImpl.isConfirmed(trxHash);
                if (isConfirmed) {
                    expectedBalance = receiver.getAmountInBtc();
                    actualBalance = blockExplorerImpl.getAddressBalance(address);
                    BigDecimal actualBalanceBIG = new BigDecimal(actualBalance);

                    if (actualBalanceBIG.equals(expectedBalance) || actualBalanceBIG.doubleValue() > expectedBalance.doubleValue()) {
                        System.out.println("Congratulations!!! Someone bought your CODE!" +
                                "\n" + "You get " + actualBalance + " BTC" +
                                "\n" + "to this address: " + address +
                                "\n" + "(actualBalance.equals(expectedBalance) & I give your source code to someone)");
                        try {
                            execute(sendUploadAction.setChatId(id));
                            execute(sendDocument.setChatId(id));
                            return "Thank you for purchase";
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            return "Server error, dont worry and tell about this situation to " + System.getenv("ownerName");    //Heroku Var
                        }
                    } else {
                        return "You send not enough money, if you *SURE* that it's mistake please contact " + System.getenv("ownerName");     //Heroku Var
                    }

                } else {
                    return "TRX UN confirmed, we should wait a bit";
                }

            } catch (Exception e) {
                System.out.println("IOException when check trx status");
                e.printStackTrace();
                return "Transaction not found";
            }
        }


            if (msg.equals("Cancel trx")) {
                address = "";
                System.out.println("Cancel trx");
                return "DONE! Cancel trx";
            }


            if (msg.equals("Exchanger")) {
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

            if (msg.contains("USD to BTC")) {
                lastMessage = msg;
                return "Type number";
            }

            if (lastMessage.contains("USD to BTC")) { //TODO Put it to Exchanger new method
                currency = "USD ";

                try {
                    value = new BigDecimal(msg);
                    lastMessage = "";
                } catch (NumberFormatException e) {
                    return "This is not a number, try again";
                }
                return currency +
                        "*" + value + "*" +
                        " = " +
                        "*" + exchanger.exchangeUSDToBTC(value).toString() + "*" +
                        " BTC";
            }

            if (msg.contains("EUR to BTC")) {
                lastMessage = msg;
                return "Type number";
            }

            if (lastMessage.contains("EUR to BTC")) {
                currency = "EUR ";

                try {
                    value = new BigDecimal(msg);
                    lastMessage = "";
                } catch (NumberFormatException e) {
                    return "This is not a number, try again";
                }
                return currency +
                        "*" + value + "*" +
                        " = " +
                        "*" + exchanger.exchangeEURToBTC(value).toString() + "*" +
                        " BTC";
            }

            if (msg.contains("PLN to BTC")) {
                lastMessage = msg;
                return "Type number";
            }

            if (lastMessage.contains("PLN to BTC")) {
                currency = "PLN ";

                try {
                    value = new BigDecimal(msg);
                    lastMessage = "";
                } catch (NumberFormatException e) {
                    return "This is not a number, try again";
                }
                return currency +
                        "*" + value + "*" +
                        " = " +
                        "*" + exchanger.exchangePLNToBTC(value).toString() + "*" +
                        " BTC";
            }

            if (msg.equals("Blockchain Checker")) {
                keyboard.clear();
                firstKeyboardRow.clear();
                secondKeyboardRow.clear();
                thirdKeyboardRow.clear();
                firstKeyboardRow.add("Check TRX by Hash");
                firstKeyboardRow.add("Check Adress Balance");
                secondKeyboardRow.add("Fork checker");
                secondKeyboardRow.add("Today's blocks quantity");
                thirdKeyboardRow.add("Menu");
                keyboard.add(firstKeyboardRow);
                keyboard.add(secondKeyboardRow);
                keyboard.add(thirdKeyboardRow);
                keyboardMarkup.setKeyboard(keyboard);

                return "What would you like to check?";
            }

            if (msg.equals("Check TRX by Hash")) {
                lastMessage = "Check TRX by Hash";
                return "Paste TRX Hash";
            }

            if (lastMessage.contains("Check TRX by Hash")) {
                String usersHash = msg;
                lastMessage = "";
                try {
                    if (blockExplorerImpl.isConfirmed(usersHash) == true) {
                        return "Tharnsaction was *confirmed*";
                    } else {
                        return "Tharnsaction is *UN confirmed*";
                    }
                } catch (APIException e) {
                    return "'Transaction *NOT* found";
                }
            }

            if (msg.equals("Check Adress Balance")) {
                lastMessage = "Check Adress Balance";
                return "Paste BTC address";
            }

            if (lastMessage.contains("Check Adress Balance")) {
                String userAddress = msg;
                lastMessage = "";
                long userBalance;
                double userBalanceBtc;
                try {
                    userBalance = blockExplorerImpl.getAddressBalance(userAddress);  //get in Satoshi
                    userBalanceBtc = (double) userBalance / 100000000; //convert to BTC
                } catch (Exception e) {
                    return "Wrong BTC address";
                }
                return userAddress
                        + "\n"
                        + "Address Balance: "
                        + "\n"
                        + userBalanceBtc
                        + " *BTC*";
            }

            if (msg.equals("Fork checker")) {
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

            if (msg.contains("Check chain for fork")) {
                System.out.println("Fork checker");
                if (blockExplorerImpl.isForked()) {
                    return "\n" + "The main chain has *forked*!" + "\n";
                } else {
                    return "\n" + "The chain is still in *one piece* :)" + "\n";
                }
            }

            if (msg.equals("Today's blocks quantity")) {
                System.out.println("Today's mined blocks");
                int numTodayBlocks;
                try {
                    numTodayBlocks = blockExplorerImpl.getTodayBlocks();
                    return "*" + numTodayBlocks + "*" + " blocks were mined today since 00:00 UTC";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "*Upsss...* Can't reach today's blocks";

                }
            }

            if (msg.equals("Features")) {
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

            if (msg.equals("BTC market price in USD")) {
                BigDecimal marketPrice;
                try {
                    marketPrice = statisticsImpl.getMarketPriceInUSD();
                    return "Market price is: " + "\n" + "*USD " + marketPrice + "*";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Can't reach market price, but it's grows :)";
                }


            }

            if (msg.equals("Hash rate")) {
                double hashRate;
                try {
                    hashRate = statisticsImpl.getHashRate();
                    return "Actual Hash Rate is: " + "\n" + "*" + hashRate + "*";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Can't reach Hash Rate, but it's grows :)";
                }
            }

            if (msg.equals("Number Of Transactions")) {
                long numberOfTrx;
                try {
                    numberOfTrx = statisticsImpl.getNumberOfTrx();
                    return "Today number of transaction is: " + "\n" + "*" + numberOfTrx + "*";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Can't reach TRX number, but it's grows :)";
                }
            }

            return "How can I help you?";


        }
    }