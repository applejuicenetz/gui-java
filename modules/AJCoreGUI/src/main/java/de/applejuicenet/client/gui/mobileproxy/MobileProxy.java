package de.applejuicenet.client.gui.mobileproxy;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Information;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MobileProxy extends Thread {
    
    private static MobileProxy instance = null;

    private String password = "test123";
    private ServerSocket serverSocket = null;

    private MobileProxy(){
        try {
            serverSocket = new ServerSocket(9852);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized MobileProxy getInstance(){
        if (null == instance){
            instance = new MobileProxy();
            instance.start();
        }
        return instance;
    }
    
    public void run() {
        while (!isInterrupted()) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                new HttpServerThread(socket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class HttpServerThread extends Thread {
        private final Socket socket;

        public HttpServerThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            HTTPRequest request = new HTTPRequest(socket);
            if (!request.parse()) {
                return;
            }
            if (request.getRequestedFile().endsWith("/aj")) {
                String pwd = request.getParameter("pwd");
                HTTPResponse response = new HTTPResponse(request);
                if (!pwd.equals(password)){
                    response.sendAnswer("hate you",
                            HTTPResponse.PlainMimeType, HTTPResponse.HTTP_OK);
                }
                ApplejuiceFassade af = AppleJuiceClient.getAjFassade();
                StringBuffer answer = new StringBuffer();
                Information inf = af.getInformation();
                String credits = inf.getCreditsAsString();
                credits = credits.replace("Credits: ", "").trim();
                answer.append(credits + ";;");
                answer.append(inf.getUpDownAsString().trim() + ";;");
                response.sendAnswer(answer.toString().trim(),
                        HTTPResponse.PlainMimeType,
                        HTTPResponse.HTTP_OK);
            }
        }
    }
}
