package client.handlers;

import static client.ui.clientFrame.isConnected;
import static client.ui.clientFrame.txtChat;
import static client.ui.clientFrame.updateComponentContextConnect;
import static client.ui.clientFrame.updateComponentContextDisconnect;
import static client.ui.clientFrame.updateDashBoard;

import java.io.BufferedReader;

import javax.swing.JOptionPane;

import client.models.Paquet;
import client.models.User;
import client.utility.Command;
import static client.utility.utils.*;

public class ClientThread implements Runnable {

    private final BufferedReader reader;

    // constructor
    public ClientThread(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        String[] data;
        String stream;
        String myUsername = "";

        try {
            while ((stream = reader.readLine()) != null) {
                data = stream.split("%%");
                System.out.println("stream lu : " + stream);

                Paquet paquet = new Paquet(new User(data[0]), data[1], data[2]);
                String username = paquet.getUser().getUsername(), message = paquet.getMessage(),
                        command = paquet.getCommand();

                switch (command) {
                    case Command.CONNECT -> {
                        txtChat.setText("");
                        myUsername = capitalize(message.split(" ")[0]);
                        updateDashBoard("SERVEUR", "Tu es à présent connecter.");
                        updateComponentContextConnect();
                        JOptionPane.showMessageDialog(null, "Bienvenue " + myUsername, "SUCCESS",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    case Command.DISCONNECT -> {
                        updateDashBoard("SERVEUR", "Tu es à présent déconnecter.");
                        updateComponentContextDisconnect();
                    }
                    case Command.CHAT -> {
                        username = username.equals(myUsername) ? "Moi" : username;
                        updateDashBoard(username, message);
                    }
                    case Command.SERVER_ERROR -> {
                        updateDashBoard(username, message);
                        isConnected = false;
                        updateComponentContextDisconnect();
                        return;
                    }
                    case Command.LIST -> {
                        // split the list of users
                        String[] partMessage = message.split(":");
                        // split partMessage
                        String[] users = partMessage[1].trim().split(" ");
                        txtChat.append(username + " : " + partMessage[0] + "\n");
                        for (String user : users) {
                            txtChat.append("     " + user + "\n");
                        }
                    }
                    default -> {
                    }
                }
            }
        } catch (Exception e) {
            // updateDashBoard("Console", e.getMessage());
            e.printStackTrace();
        }
    }
}
