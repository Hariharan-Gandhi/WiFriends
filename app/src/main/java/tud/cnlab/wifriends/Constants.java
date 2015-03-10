package tud.cnlab.wifriends;

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "tud.cnlab.wifriends.WiFriendsService.action.main";
        public static String PASSIVATE_ACTION = "tud.cnlab.wifriends.WiFriendsService.action.prev";
        public static String ACTIVATE_ACTION = "tud.cnlab.wifriends.WiFriendsService.action.play";
        public static String STARTFOREGROUND_ACTION = "tud.cnlab.wifriends.WiFriendsService.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "tud.cnlab.wifriends.WiFriendsService.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}