import info.blockchain.api.APIException;
import info.blockchain.api.statistics.Statistics;
import info.blockchain.api.statistics.StatisticsResponse;

import java.io.IOException;
import java.math.BigDecimal;

class StatisticsImpl {

    private Statistics statistics = new Statistics();

    public BigDecimal getMarketPriceInUSD() throws APIException, IOException {
        BigDecimal marketPriceInUSD;
        StatisticsResponse response = statistics.getStats();
        marketPriceInUSD = response.getMarketPriceUSD();
        return marketPriceInUSD;
    }

    public double getHashRate() throws APIException, IOException{
        double hashRate;
        StatisticsResponse response = statistics.getStats();
        hashRate = response.getHashRate();
        return hashRate;
    }

    public long getNumberOfTrx()throws APIException, IOException{
        long numberOfTrx;
        StatisticsResponse response = statistics.getStats();
        numberOfTrx = response.getNumberOfTransactions();
        return numberOfTrx;

    }

}
