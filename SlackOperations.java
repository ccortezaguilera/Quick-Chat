import javax.xml.ws.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Carlos on 1/27/2017.
 */
public class SlackOperations {
    private static final String OTHER_TOKEN = "xoxp-120869210337-131966261683-132642438227-4a57a7897b8a298c7b8ad4d1c0a6d33a";
    private static final String TEST_TOKEN = "xoxp-120869210337-131141738960-132633104067-5103018a9f3ad2e2b298b1ec3a79c897";
    private static final String CHANNEL_HISTORY = "https://slack.com/api/channels.history?";
    private static final String POST_MESSAGE = "https://slack.com/api/chat.postMessage";
    private static final String CHANNEL_LIST = "https://slack.com/api/channels.list?";

    /**
     * GETUSERS - List IDs of all users who have posted to a given channel.
     */
    public static void getUsers(Map arguments) {
        String parameters = "";
        String channel = (String) arguments.get(CommandLineParser.CHANNEL);
        String token = (String) arguments.get(CommandLineParser.TOKEN);

        PriorityQueue channelID = (PriorityQueue) getChannelList(channel, token);
        token = "token=" + token;
        String channelId = (String) channelID.remove();
        if (channelID != null) {
            try {
                parameters = token.concat("&channel=" + URLEncoder.encode(channelId, SocketCommunication.CHARSET));
            } catch (UnsupportedEncodingException uee) {
                System.err.println("The character encoding is not supported\n" + uee.getMessage());
            }
            HttpURLConnection connection = SocketCommunication.establishConnection(CHANNEL_HISTORY + parameters + "&count=1000");
            connection = doAGET(connection);
            if (connection != null) {
                PriorityQueue result = (PriorityQueue) SocketCommunication.readJSONResponse(connection);
                while (result.peek() != null) {
                    System.out.println((String) result.remove());
                }
                connection.disconnect();
            }
        }
    }

    /**
     * POST - Post a new message to a given channel.
     */
    public static void post(Map arguments) {
        String text = (String) arguments.get(CommandLineParser.MESSAGE);
        String token = (String) arguments.get(CommandLineParser.TOKEN);
        String channel = (String) arguments.get(CommandLineParser.CHANNEL);

        HttpURLConnection connection = SocketCommunication.establishConnection(POST_MESSAGE);

        try {
            connection.setDoOutput(true);
            connection.setRequestMethod(SocketCommunication.POST);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", SocketCommunication.CHARSET);
            String parameters = "token=".concat(URLEncoder.encode(token, SocketCommunication.CHARSET));
            parameters = parameters.concat("&channel=" + channel + "&text=" + URLEncoder.encode(text, SocketCommunication.CHARSET));
            connection.setRequestProperty("Content-Length", Integer.toString(parameters.length()));
            SocketCommunication.writeToOutputStream(connection, parameters);
            BufferedReader objectReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), SocketCommunication.CHARSET));
            int reading = objectReader.read();
            while (reading > 0) {
                reading = objectReader.read();
            }
            objectReader.close();
            connection.disconnect();
        } catch (ProtocolException pe) {
            System.err.println("The method cannot be reset or the requested method isn't valid for HTTP");
        } catch (UnsupportedEncodingException uee) {
            System.err.println("The character encoding is not supported\n" + uee.getMessage());
        } catch (IOException ie) {
            System.err.println("An I/O error occurred while reading the input from the post response\n" + ie.getMessage());
        }
    }

    /**
     * Get the list of all channels in the team from slack. It does not include private channels.
     **/
    private static Queue getChannelList(String channel, String token) {
        try {
            HttpURLConnection connection = SocketCommunication.establishConnection(CHANNEL_LIST + "token=" + URLEncoder.encode(token, SocketCommunication.CHARSET));
            connection = doAGET(connection);
            if (connection != null) {
                ResponseParser.setSearchKey(channel);
                ResponseParser.setgetUsers(true);
                PriorityQueue result = (PriorityQueue) SocketCommunication.readJSONResponse(connection);
                return result;
            }
        } catch (UnsupportedEncodingException uee) {
            System.err.println("The character encoding is not supported" + uee.getMessage());
        }
        return null;
    }

    /**
     * Establishing that there will be a GET HTTP request.
     *
     * @param connection - It's the HTTPURLConnection that will be doing the GET request.
     * @return null if there is an exception.
     **/
    private static HttpURLConnection doAGET(HttpURLConnection connection) {
        try {
            connection.setRequestMethod(SocketCommunication.GET);
        } catch (ProtocolException pe) {
            System.err.println("The method cannot be reset or the requested method isn't valid for HTTP\n" + pe.getMessage());
            return null;
        }
        return connection;
    }

}
