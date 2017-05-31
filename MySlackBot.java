import java.util.Map;

/**
 * Created by Carlos on 1/25/2017.
 * Slack test token xoxp-120869210337-131141738960-132633104067-5103018a9f3ad2e2b298b1ec3a79c897
 */
public class MySlackBot {
    public static void main(String[] args) {
        Map arguments = CommandLineParser.parseArguments(args);
        if (arguments != null) {
            if (arguments.containsKey("getusers")) {
                SlackOperations.getUsers(arguments);
            } else {
                SlackOperations.post(arguments);
            }
        }
    }
}
