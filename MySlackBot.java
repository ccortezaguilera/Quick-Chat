import java.util.Map;

/**
 * Created by Carlos on 1/25/2017.
 *
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
