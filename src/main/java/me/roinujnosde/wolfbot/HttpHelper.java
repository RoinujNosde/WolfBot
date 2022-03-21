package me.roinujnosde.wolfbot;

import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

public class HttpHelper {

    private static final Gson GSON = new Gson();
    private static final String USER_AGENT = "WolfBot";

    public static @Nullable <T> T get(Class<T> clazz, final String apiUrl, final Object... urlArgs) throws IOException {
        return get(clazz, Collections.emptyMap(), apiUrl, urlArgs);
    }

    public static @Nullable <T> T get(final Class<T> clazz,
                                      final Map<String, String> properties,
                                      final String apiUrl,
                                      final Object... urlArgs) throws IOException {
        URL url = new URL(String.format(apiUrl, urlArgs));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestMethod("GET");
        properties.forEach(connection::addRequestProperty);

        if (connection.getResponseCode() == 404) {
            return null;
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        T result = GSON.fromJson(reader, clazz);
        reader.close();
        return result;
    }

}
