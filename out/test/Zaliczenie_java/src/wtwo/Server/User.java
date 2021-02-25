package wtwo.Server;

import javafx.application.Platform;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class User {

    HashMap<String, DataOutputStream> clientmap = new HashMap<String, DataOutputStream>();

    String str; //this is to update ChatServer.logs

    public synchronized void AddClient(String name, Socket socket) {
        try {
            clientmap.put(name, new DataOutputStream(socket.getOutputStream()));

            sendMsg(name + " joined.", "Server");
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public synchronized void RemoveClient(String name) {
        try {
            clientmap.remove(name);
            sendMsg(name + " exit.", "Server");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendMsg(String msg, String name) throws Exception {

        str = name + " : " + msg;
        Platform.runLater(() -> {
            ChatServer.logs.setText(ChatServer.logs.getText() + "\n" + str);
        });


        Iterator iterator = clientmap.keySet().iterator();
        while (iterator.hasNext()) {
            String clientname = (String) iterator.next();
            clientmap.get(clientname).writeUTF(name + " : " + msg);
        }
    }
}
