package util;

import java.util.ArrayList;
import java.util.Random;

public class Username {
    public static ArrayList<String> usernameList = new ArrayList<String>() {{
        add("Stan");
        add("Cartman");
        add("Kenny");
        add("Kyle");
    }};
    public static String getUsername(){
        int random = new Random().nextInt(usernameList.size());
        String userToSend = usernameList.get(random);
        usernameList.remove(random);
        return userToSend;
    }
}
