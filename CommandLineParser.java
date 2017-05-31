import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carlos on 1/25/2017.
 */
public class CommandLineParser {
    protected static final String TOKEN = "token";
    protected static final String MESSAGE = "message";
    protected static final String CHANNEL = "channel";

    public static Map parseArguments(String[] args) {
        if (args.length > 6 || args.length < 5) {
            usage();
            return null;
        }
        Map arguments = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-token":
                    arguments.put(TOKEN, args[i + 1]);
                    i += 1;
                    break;
                case "-getusers":
                    arguments.put(args[i].substring(1), "");
                    break;
                case "-channel":
                    arguments.put(CHANNEL, args[i + 1]);
                    i += 1;
                    break;
                case "-post":
                    arguments.put(MESSAGE, args[i + 1]);
                    i += 1;
                    break;
                default:
                    System.out.println(args[i]);
                    break;
            }
        }
        if (arguments.containsKey("getusers") && arguments.containsKey(MESSAGE)) {
            usage();
            return null;
        }
        return arguments;
    }

    private static void usage() {
        System.out.println("Please follow one of the following formats:");
        System.out.println("java MySlackBot -token <token> -getusers -channel <channel_name>\nor");
        System.out.println("java MySlackBot -token <token> -post <message> -channel <channel_name>");
        System.out.println("\nThe arguments could go in any order.");
    }
}