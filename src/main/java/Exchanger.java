import info.blockchain.api.exchangerates.ExchangeRates;

import java.math.BigDecimal;


public class Exchanger {

    private BigDecimal USDToBTC;
    private BigDecimal EURToBTC;
    private BigDecimal PLNToBTC;
    private BigDecimal BTCToUSD;

    private ExchangeRates exchanger = new ExchangeRates();

    BigDecimal exchangeUSDToBTC(BigDecimal quantity) {
        try {
            USDToBTC = exchanger.toBTC("USD", quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return USDToBTC;
    }

    BigDecimal exchangeEURToBTC(BigDecimal quantity) {
        try {
            EURToBTC = exchanger.toBTC("EUR", quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EURToBTC;
    }

    BigDecimal exchangePLNToBTC(BigDecimal quantity) {
        try {
            PLNToBTC = exchanger.toBTC("PLN", quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PLNToBTC;
    }

}


