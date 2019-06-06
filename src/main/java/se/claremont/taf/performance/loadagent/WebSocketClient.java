package se.claremont.taf.performance.loadagent;


import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient {

    protected WebSocketClient(String url){
        try {
            final WebSocketClientEndpoint clientEndPoint = new WebSocketClientEndpoint(new URI(url));

            clientEndPoint.addMessageHandler(new WebSocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });
            clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
