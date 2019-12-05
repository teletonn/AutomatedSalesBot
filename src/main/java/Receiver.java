import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import info.blockchain.api.APIException;
import info.blockchain.api.HttpClient;
import info.blockchain.api.receive.Receive;
import info.blockchain.api.receive.ReceiveResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



class Receiver {

    private BigDecimal amountInBtc;

    Xpub xpub = new Xpub();


    public ReceiveResponse receive(String xPUB, String callbackUrl) throws APIException, IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("xpub", xPUB);
        params.put("callback", callbackUrl);
        params.put("key", System.getenv("BTCApiKey"));    //Heroku Var

        if (checkXpubGap(xPUB) <= 19) {
            String response = HttpClient.getInstance().get("https://api.blockchain.info/", "v2/receive", params);
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(response).getAsJsonObject();
            ReceiveResponse res = new ReceiveResponse(obj.get("index").getAsInt(), obj.get("address").getAsString(), obj.get("callback").getAsString());

            System.out.println("Index: " + res.getIndex());
            System.out.println("Receiving Address is: " + res.getReceivingAddress());

            return res;
        } else {
            List<String> mylist = xpub.xpubListCreator();
            System.out.println(mylist);
            xpub.getNewXpub(mylist);
            while (checkXpubGap(xpub.getxPub()) > 19) {
                xpub.getNewXpub(mylist);
                System.out.println("New xpub is: " + xpub.getxPub());
            }
            return receive(xpub.getxPub(), callbackUrl);
        }
    }

    public int checkXpubGap(String xPub) {
        Receive receive = new Receive(System.getenv("BTCApiKey"));    //Heroku Var
        int xpubGap;
        try {
            xpubGap = receive.checkGap(xPub);
            System.out.println("Gap is : " + xpubGap);
        } catch (Exception e) {
            System.out.println("Exception when checkGap");
            e.printStackTrace();
            xpubGap = 0;
        }
        return xpubGap;
    }

    public BigDecimal getAmountInBtc() {
        return amountInBtc;
    }


    public void setActualPriceInBtc() {
        Exchanger exchanger = new Exchanger();
        BigDecimal actualAmount;
        actualAmount = exchanger.exchangeUSDToBTC(new BigDecimal(System.getenv("PriceInUsd")));    //Heroku Var
        amountInBtc = actualAmount;
    }

}
