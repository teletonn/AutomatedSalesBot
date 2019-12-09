import info.blockchain.api.APIException;
import info.blockchain.api.blockexplorer.entity.*;
import info.blockchain.api.blockexplorer.*;

import java.io.IOException;
import java.util.List;

class BlockExplorerImpl {

    private BlockExplorer blockExplorer = new BlockExplorer();

    public long getAddressBalance(String address) throws APIException, IOException{

        Address userAddress = blockExplorer.getAddress(address);
        long userBalance = userAddress.getFinalBalance();
        return userBalance;
    }

    public int getTodayBlocks() throws APIException, IOException{
        List<SimpleBlock> todaysBlocks = blockExplorer.getBlocks();
        int numberOfTodaysBlocks = (todaysBlocks.size());
        return numberOfTodaysBlocks;
    }

    public boolean isForked(){
        try {
            // get the latest block on the main chain and read its height
            LatestBlock latestBlock = blockExplorer.getLatestBlock();
            long latestBlockHeight = latestBlock.getHeight();
            // use the previous block height to get a list of blocks at that height
            // and detect a potential chain fork
            List<Block> blocksAtHeight = blockExplorer.getBlocksAtHeight(latestBlockHeight);
            System.out.println("List created");
            if (blocksAtHeight.size() > 1) {
                System.out.println("The chain has forked!");
                return true;
            } else {
                System.out.println("The chain is still in one piece :)");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public String getTrxHash(String address) throws APIException, IOException {
        Address userAddress = blockExplorer.getAddress(address);
        List<Transaction> transactions = userAddress.getTransactions();
        String trxHash;
        try {
            trxHash = transactions.get(0).getHash();
            System.out.println("TRX HASH :" + trxHash);
        } catch (IndexOutOfBoundsException e) {
            return "TRX NOT FOUND, you should pay - than check";
        }
        return trxHash;
    }

    public boolean isConfirmed(String hash) throws APIException {
        boolean isConf = false;
        try {
            Transaction trx = blockExplorer.getTransaction(hash);

            if (trx.getBlockHeight() != -1) {
                isConf = true;
            }
        }catch (IOException e){
            e.printStackTrace();

        }
        return isConf;
    }

    public Long adminBalance(){
        long balance = 1;
        try {
            XpubFull xPub1 = blockExplorer.getXpub(System.getenv("xPub1"), FilterType.All, 10, 5);
            XpubFull xPub2 = blockExplorer.getXpub(System.getenv("xPub1"), FilterType.All, 10, 5);
            XpubFull xPub3 = blockExplorer.getXpub(System.getenv("xPub1"), FilterType.All, 10, 5);
            XpubFull xPub4 = blockExplorer.getXpub(System.getenv("xPub1"), FilterType.All, 10, 5);
            XpubFull xPub5 = blockExplorer.getXpub(System.getenv("xPub1"), FilterType.All, 10, 5);

            balance = xPub1.getFinalBalance() +
                      xPub2.getFinalBalance() +
                      xPub3.getFinalBalance() +
                      xPub4.getFinalBalance() +
                      xPub5.getFinalBalance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return balance;
    }
}
