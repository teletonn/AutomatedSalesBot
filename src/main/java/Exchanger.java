import info.blockchain.api.exchangerates.ExchangeRates;

import java.math.BigDecimal;


public class Exchanger {

    private BigDecimal MoneyToBTC;

    private ExchangeRates exchanger = new ExchangeRates();

    BigDecimal exchange(BigDecimal quantity, String currency) {
        try {
            MoneyToBTC = exchanger.toBTC(currency, quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MoneyToBTC;
    }

}


