package wtwo.Server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Class ChatService is to run a thread which reads stream from client
//
public class ChatService implements Runnable {

    Socket socket;
    DataInputStream in;
    String name;
    String password;
    User user = new User();
    String register;

    public ChatService(User user, Socket socket) throws Exception {
        this.user = user;
        this.socket = socket;

        in = new DataInputStream(socket.getInputStream());
        // getInputStream from the socket to read data from the client who has been connected

        this.register = in.readUTF();
        this.password = in.readUTF();
        this.name = in.readUTF(); // To read a name


//      System.out.println(password+ " " + name + " " + register);

        if (register.equals("true")) {
            System.out.println("test");
            addNewAcc.addUserToFile(name, password);
        }

        if (validate(name, password) == true) {
            user.AddClient(name, socket);
        } // To add a new user (Management of users in the chat room)
    }

    public void run() {
        try {
            while (true) {
                String msg = in.readUTF(); // read stream from client
                user.sendMsg(msg, name);
            }
        } catch (Exception e) {
            user.RemoveClient(this.name);
        }
    }

    public static boolean validate(String login, String haslo) {
        Scanner sc = null;

        try {
            sc = new Scanner(new File("K:\\zajecia_java21\\Zaliczenie_java\\src\\wtwo\\Server\\login.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] userData = line.split(";");
            if (login.equals(userData[0]) && haslo.equals(userData[1])) {
                return true;
            }
        }
        return false;
    }
}

class addNewAcc {
    public static boolean addUserToFile(String login, String password) {

        if (alreadyExist(login)) {
            return false;
        }
        try {
            FileWriter file = new FileWriter("K:\\zajecia_java21\\Zaliczenie_java\\src\\wtwo\\Server\\login.txt", true);
            BufferedWriter out = new BufferedWriter(file);
            out.write(login + ";" + password + "\n");
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean alreadyExist(String login) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File("K:\\zajecia_java21\\Zaliczenie_java\\src\\wtwo\\Server\\login.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] userData = line.split(";");

            if (login.equals(userData[0])) {
                return true;
            }
        }

        return false;
    }
}