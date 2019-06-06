package se.claremont.taf.performance.logserver;

import se.claremont.taf.performance.TestRunner;
import se.claremont.taf.performance.log.LogPost;
import se.claremont.taf.performance.log.LogPostDecoder;
import se.claremont.taf.performance.log.LogPostEncoder;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/log", decoders = LogPostDecoder.class, encoders = LogPostEncoder.class)
public class LogServerMappingsControler {
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("New session '" + session.toString() + "'.");
    }

    @OnMessage
    public void onMessage(Session session, LogPost logPost) throws IOException {
        TestRunner.getInstance().executionLog.log(logPost);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("Session '" + session.toString() + "' closed.");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("OUPS! Session '" + session + "' throwed: " + throwable.toString());
    }
}
