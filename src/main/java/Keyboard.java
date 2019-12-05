import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

public class Keyboard {
    ArrayList<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
    KeyboardRow firstKeyboardRow = new KeyboardRow();
    KeyboardRow secondKeyboardRow = new KeyboardRow();
    KeyboardRow thirdKeyboardRow = new KeyboardRow();

    public void createKeyboard(ReplyKeyboardMarkup keyboardMarkup){

        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
    }
}
