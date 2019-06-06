package se.claremont.taf.performance.log;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class LogPostEncoder implements Encoder.Text<LogPost> {

    private static Gson gson = new Gson();

    @Override
    public String encode(LogPost logPost) throws EncodeException {
        return gson.toJson(logPost);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
