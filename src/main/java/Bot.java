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

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;


public class Bot extends TelegramLongPollingBot {

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
    Messages messages = new Messages();


    public void onUpdateReceived(Update update) {
        update.getUpdateId();

        xPubInit();

        sendTypeAction.setChatId(update.getMessage().getChatId());
        sendTypeAction.setAction(ActionType.TYPING);
        sendUploadAction.setAction(ActionType.UPLOADDOCUMENT);

        try {
            URL url = new URL(System.getenv("FileUrl"));    //Heroku Var
            String fileName = System.getenv("FileName");    //Heroku Var
            InputStream stream = url.openStream();
            sendDocument.setChatId(update.getMessage().getChatId()).setDocument(fileName,stream);

        }catch (Exception e){
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

        messages.createKeyboard(keyboardMarkup);

        if (msg.equals("/start") || msg.equals("Menu") || msg.equals("Hello")) {
            return messages.startMessage(keyboardMarkup);
        }

        if (msg.equals("Buy source code")) {
            return messages.buySourceCode(keyboardMarkup);
        }

        if (msg.equals("Info about")) {
            return messages.infoAbout(keyboardMarkup);
        }

        if (msg.equals("Start preparing transaction")) {
           return messages.preparinTransaction(keyboardMarkup);
        }

        if (msg.equals("Check trx status")) {
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
               return messages.exchangeMessage(keyboardMarkup);
            }

            if (msg.contains("USD to BTC")) {
                lastMessage = msg;
                return "Type number";
            }

            if (lastMessage.contains("USD to BTC")) { //TODO Put it to Exchanger new method
                currency = "USD";

                try {
                    value = new BigDecimal(msg);
                    lastMessage = "";
                } catch (NumberFormatException e) {
                    return "This is not a number, try again";
                }
                return currency +
                        "*" + value + "*" +
                        " = " +
                        "*" + exchanger.exchange(value, currency).toString() + "*" +
                        " BTC";
            }

            if (msg.contains("EUR to BTC")) {
                lastMessage = msg;
                return "Type number";
            }

            if (lastMessage.contains("EUR to BTC")) {
                currency = "EUR";

                try {
                    value = new BigDecimal(msg);
                    lastMessage = "";
                } catch (NumberFormatException e) {
                    return "This is not a number, try again";
                }
                return currency +
                        "*" + value + "*" +
                        " = " +
                        "*" + exchanger.exchange(value,currency).toString() + "*" +
                        " BTC";
            }

            if (msg.contains("PLN to BTC")) {
                lastMessage = msg;
                return "Type number";
            }

            if (lastMessage.contains("PLN to BTC")) {
                currency = "PLN";

                try {
                    value = new BigDecimal(msg);
                    lastMessage = "";
                } catch (NumberFormatException e) {
                    return "This is not a number, try again";
                }
                return currency +
                        "*" + value + "*" +
                        " = " +
                        "*" + exchanger.exchange(value, currency).toString() + "*" +
                        " BTC";
            }

            if (msg.equals("Blockchain Checker")) {
               return messages.blockChainCheker(keyboardMarkup);
            }

            if (msg.equals("Check TRX by Hash")) {
                lastMessage = "Check TRX by Hash";
                return "Paste TRX Hash";
            }

            if (lastMessage.contains("Check TRX by Hash")) {
                String usersHash = msg;
                lastMessage = "";
                try {
                    if (blockExplorerImpl.isConfirmed(usersHash)) {
                        return "Transaction was *confirmed*";
                    } else {
                        return "Transaction is *UN confirmed*";
                    }
                } catch (APIException e) {
                    return "'Transaction *NOT* found";
                }
            }

            if (msg.equals("Check Address Balance")) {
                lastMessage = "Check Address Balance";
                return "Paste BTC address";
            }

            if (lastMessage.contains("Check Address Balance")) {
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
               return messages.forkChecker(keyboardMarkup);
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
               return messages.features(keyboardMarkup);
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