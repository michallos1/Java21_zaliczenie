package wtwo.Client;

import java.io.*;
import java.util.Scanner;

public class Register {
    public static boolean addUserToFile(String login, String password) {
        if(alreadyExist(login)) {
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
