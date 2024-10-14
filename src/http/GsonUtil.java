package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonUtil {

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())  // Register LocalDateTime adapter
                .registerTypeAdapter(Duration.class, new DurationAdapter())  // Register Duration adapter
                .create();
    }
}
