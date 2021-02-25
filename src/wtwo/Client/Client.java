package wtwo.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

public class Client extends Application {
    public static String appInfo = "Test";
    public static Label logs = new Label("[Chat Room Begins]");
    private TextField enterName = new TextField();
    private TextField enterPassword = new TextField();
    private TextField regPassword = new TextField();
    private TextField regUsername = new TextField();
    private TextField weatherInfo = new TextField();
    public static TextField enterMessage = new TextField();
    private Scene scene1, scene2, scene3, scene4, scene5;
    private Button submitName = new Button("Join Chatroom");
    private Button buttonRegister = new Button("Register");
    private Button buttonMenu = new Button("Menu");
    private Button buttonRegisterAdd = new Button("Add new account");
    private Button weather = new Button("Weather");


    public static String name = "";
    public static String password = "";
    public static String rPassword = "";
    public static String rName = "";
    public static String weatherInfoData;
    public static String register = "false";

    @Override
    public void start(Stage primaryStage) throws Exception {

        // scene1: enter name first
        GridPane root1 = new GridPane();
        root1.setPrefSize(400, 200);
        root1.setPadding(new Insets(0, 20, 20, 20)); // add 10px padding
        root1.setVgap(15); // set vertical gap
        root1.setHgap(5); // set horizontal gap
        root1.setAlignment(Pos.CENTER);
        root1.add(new Label("User name"), 0, 0);
        root1.add(enterName, 1, 0);
        root1.add(new Label("Password"), 0, 1);
        root1.add(enterPassword, 1, 1);
        root1.add(submitName, 0, 2);
        root1.add(buttonRegister, 1, 2);

        scene1 = new Scene(root1);
        primaryStage.setScene(scene1);
        primaryStage.setTitle("Chat Room");
        primaryStage.show();

        ConnectServer connectServer = new ConnectServer();

        buttonRegister.setOnAction(e -> {
            GridPane root2 = new GridPane();
            root2.setPrefSize(400, 200);
            root2.setPadding(new Insets(0, 20, 20, 20)); // add 10px padding
            root2.setVgap(15); // set vertical gap
            root2.setHgap(5); // set horizontal gap
            root2.setAlignment(Pos.CENTER);
            root2.add(new Label("User name"), 0, 0);
            root2.add(regUsername, 1, 0);
            root2.add(new Label("Password"), 0, 1);
            root2.add(regPassword, 1, 1);
            root2.add(buttonRegisterAdd, 0, 2);
            root2.add(buttonMenu, 1, 2);


            scene3 = new Scene(root2);
            primaryStage.setScene(scene3);
//            primaryStage.setTitle("Chat Room");
            primaryStage.show();

        });

        buttonRegisterAdd.setOnAction(e -> {
            rName = regUsername.getText();
            rPassword = regPassword.getText();

            Thread connectServerThread = new Thread(connectServer);
            connectServerThread.start();

            GridPane root3 = new GridPane();
            root3.setPrefSize(400, 200);
            root3.setPadding(new Insets(0, 20, 20, 20)); // add 10px padding
            root3.setVgap(15); // set vertical gap
            root3.setHgap(5); // set horizontal gap
            root3.setAlignment(Pos.CENTER);

            if ((!rName.isEmpty() && !rPassword.isEmpty())) {
                this.register = "true";
            }


            root3.add(buttonMenu, 1, 0);
            scene4 = new Scene(root3);
            primaryStage.setScene(scene4);
            primaryStage.show();



        });

        buttonMenu.setOnAction(e -> {
            primaryStage.setScene(scene1);
            primaryStage.show();
        });


        submitName.setOnAction(e -> {
            name = enterName.getText();
            password = enterPassword.getText();

            if (validate(name, password)) {
                Thread connectServerThread = new Thread(connectServer);
                connectServerThread.start();

                ScrollPane layout = new ScrollPane();
                layout.setPrefSize(400, 600);
                layout.setContent(logs);

                GridPane w1 = new GridPane();
                w1.setPrefSize(25, 25);
                w1.setPadding(new Insets(0, 0, 0, 0)); // add 10px padding
                w1.setVgap(1); // set vertical gap
                w1.setHgap(1); // set horizontal gap
                w1.add(weatherInfo, 0, 0);
                w1.add(weather, 1, 0);

                BorderPane root2 = new BorderPane();
                root2.setPrefSize(350, 400);
                root2.setCenter(layout);
                root2.setBottom(enterMessage);
                root2.setTop(w1);

                scene2 = new Scene(root2);
                primaryStage.setScene(scene2);
            }

        });

        weather.setOnAction(e -> {
            weatherInfoData = weatherInfo.getText();
            Weather w = new Weather(weatherInfoData);
            w.runWeather();
            appInfo = w.toString();

            GridPane w1 = new GridPane();
            w1.setPrefSize(300, 200);
            w1.setPadding(new Insets(0, 0, 0, 0)); // add 10px padding
            w1.setVgap(1); // set vertical gap
            w1.setHgap(1); // set horizontal gap
            w1.add(new Label(appInfo), 0, 0);
            w1.add(submitName, 0, 1);

            scene5 = new Scene(w1);
            primaryStage.setScene(scene5);
            primaryStage.show();

        });

        // scene2 -- enterMessage
        // when user enters 'enter key', get the message and send it to Server.
        Client.enterMessage.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                DataOutputStream out = connectServer.getDataOuputStream();
                String msg = Client.enterMessage.getText();
                try {
                    out.writeUTF(msg); //send msg to ChatServer through DataOutputStream
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                enterMessage.setText(""); //clear the TextField
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
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

class ConnectServer implements Runnable {
    Socket socket = null; // To open communication
    DataInputStream in = null; // To read data from Server
    DataOutputStream out = null; // To send data to Server

    public DataOutputStream getDataOuputStream() {
        return out;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", 12345);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            if (Client.register == "true") {
                out.writeUTF(Client.register);
                out.writeUTF(Client.rPassword);
                out.writeUTF(Client.rName);
                socket.close();
                Client.register = "false";
            }

            if ((!Client.name.isEmpty() && !Client.password.isEmpty())) {
                out.flush();
                out.writeUTF(Client.register);
                out.writeUTF(Client.password);
                out.writeUTF(Client.name); // send 'name' to Server}
            }

//            out.writeUTF();

            System.out.println(Client.name + " : succesfully joined the chat room: ");

        } catch (IOException e) {
        }

        if ((!Client.name.isEmpty() && !Client.password.isEmpty())) {
            try {
                // This loop is to keep reading data from Server
                while (true) {
                    String str = in.readUTF();
                    Platform.runLater(() -> {
                        Client.logs.setText(Client.logs.getText() + "\n" + str);
                    });
                }
            } catch (IOException e) {
            }

        }
    }
}

//class addNewAcc {
//    public static boolean addUserToFile(String login, String password, String reg) {
//        if (reg == "true") {
//            if (alreadyExist(login)) {
//                return false;
//            }
//            try {
//                FileWriter file = new FileWriter("K:\\zajecia_java21\\Zaliczenie_java\\src\\wtwo\\Server\\login.txt", true);
//                BufferedWriter out = new BufferedWriter(file);
//                out.write(login + ";" + password + "\n");
//                out.close();
//                return true;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
//
//    public static boolean alreadyExist(String login) {
//        Scanner sc = null;
//        try {
//            sc = new Scanner(new File("K:\\zajecia_java21\\Zaliczenie_java\\src\\wtwo\\Server\\login.txt"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        while (sc.hasNextLine()) {
//            String line = sc.nextLine();
//            String[] userData = line.split(";");
//
//            if (login.equals(userData[0])) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//}

class Weather {
    String API_KEY = "3416857f3eabcf29e6f683aec66b5eb7";
    String urlString;
    BufferedReader reader;
    String line;
    StringBuffer responseContent = new StringBuffer();
    private HttpURLConnection connection;
    String country;
    Float temp;
    String description;
    Integer pressure;
    String city;

    public Weather(String city) {
        this.city = city;
        this.urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=3416857f3eabcf29e6f683aec66b5eb7&units=metric";
    }

    public void runWeather() {
        try {
            URL url = new URL(this.urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                weatheParse(responseContent.toString());
                reader.close();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    public void weatheParse(String httpResponse) {

        JSONObject list = new JSONObject(httpResponse);
//            JSONObject obList = list.getJSONObject("weather");
//        System.out.println("\n " + list);
        this.temp = list.getJSONObject("main").getFloat("temp");
        this.country = list.getJSONObject("sys").getString("country");
        this.pressure = list.getJSONObject("main").getInt("pressure");
        this.description = list.getJSONArray("weather").getJSONObject(0).getString("description");
    }

    @Override
    public String toString() {
        return "Weather: " + '\n' +
                " city : " + city + '\n' +
                " country : '" + country + '\n' +
                " temp : " + temp + '\n' +
                " description : " + description + '\n' +
                " pressure : " + pressure;
    }
}

