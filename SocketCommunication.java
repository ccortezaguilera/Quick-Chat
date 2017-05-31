import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Carlos on 1/28/2017.
 */
public class SocketCommunication {
    protected static final String CHARSET = "UTF-8";
    protected static final String POST = "POST";
    protected static final String PUT = "PUT";
    protected static final String GET = "GET";
    protected static final String HEAD = "HEAD";

    /** Opens the connection with HTTPURLConnection
     * @return the connection
     * @exception IOException The exception is handled and will return null.
     **/
    protected static HttpURLConnection establishConnection(String url) {
        URL resource;
        try {
            resource = new URL(url);
        } catch (MalformedURLException m) {
            System.err.println("No protocol is specified, or an unknown protocol is found, or parameter is null");
            return null;
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) resource.openConnection();
        } catch (IOException ie) {
            System.err.println("An I/O error occurs while opening the connection.");
            return null;
        }

        return connection;
    }

    /** Writes the data to the server. Hence, it writes the request.
     **/
    protected static void writeToOutputStream(HttpURLConnection connection, String data) {
        OutputStreamWriter out;
        try {
            out = new OutputStreamWriter(connection.getOutputStream());
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException ie) {
            System.err.println("An I/O error occured while opening the output.\n" + ie.getMessage());
            return;
        }
    }

    /** Reads the JSONResponse
     */
    protected static Queue readJSONResponse(HttpURLConnection connection) {
        try {
            JsonReader objectReader = new JsonReader(new InputStreamReader(connection.getInputStream(), CHARSET));
            PriorityQueue result = (PriorityQueue) ResponseParser.parseJSON(objectReader);
            objectReader.close();
            return result;
        } catch (IOException ie) {
            System.err.println("An I/O error occurs while opening the input\n" + ie.getMessage());
            return null;
        }

    }

}
