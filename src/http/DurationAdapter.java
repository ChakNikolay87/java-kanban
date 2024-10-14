package http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());  // Serializes the duration as an ISO-8601 string, e.g., "PT15M"
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        if (in.peek() == null) {
            return null;
        } else {
            return Duration.parse(in.nextString());  // Parses the ISO-8601 string back into a Duration
        }
    }
}
