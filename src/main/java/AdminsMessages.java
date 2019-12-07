import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class AdminsMessages extends Messages {

    public String startAdminMessage(ReplyKeyboardMarkup keyboardMarkup) {
        keyboard.clear();
        firstKeyboardRow.clear();
        secondKeyboardRow.clear();
        thirdKeyboardRow.clear();
        firstKeyboardRow.add("Buy source code");
        secondKeyboardRow.add("Exchanger");
        secondKeyboardRow.add("Blockchain Checker");
        secondKeyboardRow.add("Features");
        thirdKeyboardRow.add("Admin Menu");
        keyboard.add(firstKeyboardRow);
        keyboard.add(secondKeyboardRow);
        keyboard.add(thirdKeyboardRow);
        keyboardMarkup.setKeyboard(keyboard);
        return "Welcome Admin: " + "*" + System.getenv("ownerName") + "*";    //Heroku Var
    }

    public String adminMenu (ReplyKeyboardMarkup keyboardMarkup){
        keyboard.clear();
        firstKeyboardRow.clear();
        secondKeyboardRow.clear();
        thirdKeyboardRow.clear();
        firstKeyboardRow.add("Check Bot Users");
        secondKeyboardRow.add("Check what users checked");
        secondKeyboardRow.add("Check your balance");
        secondKeyboardRow.add("Admin Features");
        thirdKeyboardRow.add("Menu");
        keyboard.add(firstKeyboardRow);
        keyboard.add(secondKeyboardRow);
        keyboard.add(thirdKeyboardRow);
        keyboardMarkup.setKeyboard(keyboard);
        return "What Admin wants to do?";
    }

    public String checkBotUsers (ReplyKeyboardMarkup keyboardMarkup){
        keyboard.clear();
        firstKeyboardRow.clear();
        secondKeyboardRow.clear();
        thirdKeyboardRow.clear();
        firstKeyboardRow.add("Check quantity");
        firstKeyboardRow.add("Check Users List");
        secondKeyboardRow.add("Menu");
        keyboard.add(firstKeyboardRow);
        keyboard.add(secondKeyboardRow);
        keyboardMarkup.setKeyboard(keyboard);
        return "What Admin wants to do next?";
    }

}
