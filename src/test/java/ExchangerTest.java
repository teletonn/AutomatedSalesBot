import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ExchangerTest {
    Exchanger exchanger = new Exchanger();

    @Test
    public void exchange() {
        BigDecimal Usd = new BigDecimal(1000);
        double expectedBtc = 0.13561717;    //check actual price USD/BTC
        String currency = "USD";  //"EUR", "PLN"
        double delta = 0.0;

        BigDecimal actualBtc = exchanger.exchange(Usd, currency);

        Assert.assertEquals(expectedBtc, actualBtc.doubleValue(),delta);
    }
}