import info.blockchain.api.blockexplorer.BlockExplorer;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockExplorerImplTest {

    String testAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa";  //Genesis Wallet

    BlockExplorerImpl blockExplorer = new BlockExplorerImpl();
    @Test
    public void getAddressBalance() throws Exception{

        double expectedBalance = 68.13133419;
        double delta =0.00000019;

        long actualBalance = blockExplorer.getAddressBalance(testAddress);
        double actualBalanceD = (double) actualBalance/100000000;

        Assert.assertEquals(expectedBalance,actualBalanceD,delta);

    }

    @Test
    public void getTrxHash() throws Exception{
        String expectedTrxHash = "d350959c5da2b5697c5488a3c3a901475006972e36cf204098ce8a90bfcc9549";    //get last HASH
        String actualTrxHash = blockExplorer.getTrxHash(testAddress);

        Assert.assertEquals(expectedTrxHash,actualTrxHash);

    }

    @Test
    public void isConfirmed() throws Exception{
        boolean expected = true;
        boolean actual = blockExplorer.isConfirmed("d350959c5da2b5697c5488a3c3a901475006972e36cf204098ce8a90bfcc9549");

        Assert.assertEquals(expected,actual);
    }
}