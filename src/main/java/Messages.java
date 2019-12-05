import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

public class Messages {

    ArrayList<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
    KeyboardRow firstKeyboardRow = new KeyboardRow();
    KeyboardRow secondKeyboardRow = new KeyboardRow();
    KeyboardRow thirdKeyboardRow = new KeyboardRow();

    public void createKeyboard(ReplyKeyboardMarkup keyboardMarkup){

        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
    }
    public String startMessage(ReplyKeyboardMarkup keyboardMarkup){
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

    public String buySourceCode(ReplyKeyboardMarkup keyboardMarkup){
        keyboard.clear();
        firstKeyboardRow.clear();
        secondKeyboardRow.clear();
        firstKeyboardRow.add("Info about");
        firstKeyboardRow.add("Start preparing transaction");
        secondKeyboardRow.add("Check trx status");
        secondKeyboardRow.add("Menu");
        keyboard.add(firstKeyboardRow);
        keyboard.add(secondKeyboardRow);
        keyboardMarkup.setKeyboard(keyboard);

        return "What's next?";
    }
}
