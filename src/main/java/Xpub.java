import java.util.ArrayList;
import java.util.List;

public class Xpub {

    private String xPub = "";

    public String getxPub() {
        return xPub;
    }

    public List<String> xpubListCreator() {
        List<String> xpubList = new ArrayList<String>();

        xpubList.add(System.getenv("xPub1"));    //Heroku Var
        xpubList.add(System.getenv("xPub2"));    //Heroku Var
        xpubList.add(System.getenv("xPub3"));    //Heroku Var
        xpubList.add(System.getenv("xPub4"));    //Heroku Var
        xpubList.add(System.getenv("xPub5"));    //Heroku Var
        return xpubList;
    }

    public String getNewXpub(List<String> xpubList) {
        String lastXpub = "";
        int counter = 0;

        if (xPub.equals("")) {
            xPub = xpubList.get(0);
            lastXpub = xpubList.get(0);
            counter++;

        } else if (xPub.equals(xpubList.get(0))) {
            for(int j = 1; j < xpubList.size(); j++ ) {
                xPub = xpubList.get(counter);
                counter++;
            }
        } else if(counter == (xpubList.size() - 1)){
            System.out.println("You dont have unused xpubs, create and change values on Heroku");
            return "Connecting problems, don't worry ;) please contact " + System.getenv("ownerName");     //Heroku Var;
        }

        return xPub;
    }


}
