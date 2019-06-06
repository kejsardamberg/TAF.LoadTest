package se.claremont.taf.performance.log;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.Date;

public class LogPost {
    Date timeStamp;
    Severity severity;
    String message;

    public LogPost(Severity severity, String message){
        timeStamp = new Date();
        this.message = message;
        this.severity = severity;
    }

}
