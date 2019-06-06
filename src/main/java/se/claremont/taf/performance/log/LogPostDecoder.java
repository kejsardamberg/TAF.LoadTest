package se.claremont.taf.performance.log;

import com.google.gson.Gson;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class LogPostDecoder implements Decoder.Text<LogPost> {

    private static Gson gson = new Gson();

    @Override
    public LogPost decode(String s) throws DecodeException {
        return gson.fromJson(s, LogPost.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
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