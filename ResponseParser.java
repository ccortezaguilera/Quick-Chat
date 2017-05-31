import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Carlos on 1/28/2017.
 */
public class ResponseParser {


    private static String searchKey;
    private static boolean getUsers;

    protected static Queue parseJSON(JsonReader reader) {
        Queue result = new PriorityQueue<>();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                boolean bool = reader.nextBoolean();
                String nextName = reader.nextName();
                match(reader, result);
            }
            reader.endObject();
        } catch (IOException ie) {
            System.err.println("An I/O error occurred while beginning the parse\n" + ie.getMessage());
            return null;
        }
        setSearchKey("");
        setgetUsers(false);
        return result;
    }

    protected static void parseJSONArray(JsonReader reader, Queue result) {
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                match(reader, result);
            }
            reader.endArray();
        } catch (IOException ie) {
            System.err.println("An I/O error occurred while parsing the JSONArray\n" + ie.getMessage());
            return;
        }
    }

    protected static void parseJSONObject(JsonReader reader, Queue result) {
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                parseJSONKey(reader, result);
            }
            reader.endObject();
        } catch (IOException ie) {
            System.err.println("An I/O error occurred while parsing the JSONObject\n" + ie.getMessage());
        }
    }

    protected static void parseJSONKey(JsonReader reader, Queue result) {
        try {
            if (reader.peek() != JsonToken.NAME)
                return;
            String key = reader.nextName();
            if (key.equals("user") && !getUsers) {
                String user = reader.nextString();
                result.add(user);
                match(reader, result);
            } else if (key.equals("id")) {
                String channel_id = findName(reader);
                if (channel_id == null) {
                    parseJSONKey(reader, result);
                } else {
                    result.add(channel_id);
                }
            } else {
                match(reader, result);
            }
        } catch (IOException ie) {
            System.err.println("An I/O error occurred while parsing the JSONKeys\n" + ie.getMessage());
        }
    }

    protected static void match(JsonReader reader, Queue result) {
        try {
            JsonToken next = reader.peek();
            if (next == JsonToken.BEGIN_ARRAY) {
                parseJSONArray(reader, result);
            } else if (next == JsonToken.BEGIN_OBJECT) {
                parseJSONObject(reader, result);
            } else if (next == JsonToken.BOOLEAN) {
                reader.nextBoolean();
            } else if (next == JsonToken.NAME) {
                parseJSONKey(reader, result);
            } else if (next == JsonToken.NULL) {
                reader.nextNull();
            } else if (next == JsonToken.NUMBER) {
                reader.nextLong();
            } else if (next == JsonToken.STRING) {
                reader.nextString();
            } else if (next == JsonToken.END_OBJECT) {
                return;
            }
            if (reader.hasNext())
                match(reader, result);
        } catch (IOException ie) {
            System.err.println("An I/O error occurred while matching\n" + ie.getMessage());
        }
    }

    protected static String findName(JsonReader reader) {
        try {
            String channelId = reader.nextString();
            if (reader.peek() == JsonToken.END_OBJECT)
                return null;
            String channelKeyName = reader.nextName();
            String channelName = reader.nextString();
            if (channelName.equals(searchKey)) {
                return channelId;
            }
        } catch (IOException ie) {
            System.err.println("An I/O error occurred while searching for Name\n" + ie.getMessage());
        }
        return null;
    }

    /**
     * Sets the key we want to find in the JSON Object.
     */
    protected static void setSearchKey(String key) {
        searchKey = key;
    }

    protected static void setgetUsers(boolean yes) {
        getUsers = yes;
    }
}
