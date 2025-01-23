package main;

import entity.Player;

import java.io.*;
import java.net.*;

public class Network {
    Player player;
    GamePanel gp;

    int port;
    String ip;

    public ServerSocket serverSocket;
    public Socket clientSocket;

    public DataInputStream in;
    public DataOutputStream out;

    public volatile boolean connected;

    public Network(GamePanel gp) {
//        this.player = player;
        this.gp = gp;

//        port = 12345;
    }

    public void Host() throws IOException {
        // Starts hosting a game on the port assigned

        // If there's a socket open already, close and remove it
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                serverSocket = null;
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                clientSocket = null;
            }

            if (out != null) {
                in.close();
                in = null;
                out.close();
                out = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Create a new network socket to host the game with
            serverSocket = new ServerSocket();
            // Make it reusable so the player doesn't have to reopen the game if they want to host again after playing
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));

            System.out.println("Host started. Waiting for a client to connect...");

            // Connect to the other player and assign them to the clientSocket
            // This function will block the thread until another player is connected, which is why it's run on a separate thread
            clientSocket = serverSocket.accept();
            System.out.println("Client connected from " + clientSocket.getInetAddress());

            // Update the connected status (used for other functions to know if you're in a multiplayer or single player game, also used to check if this function is finished running)
            connected = true;

            // Set up input and output streams to be able to send/receive data between the two players
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            // Start a thread to constantly listen for incoming data
            new Thread(() -> listenForMessages(in)).start();

        } catch (IOException e) {
            // Make sure connected is false if something goes wrong
            connected = false;
            if (Thread.currentThread().isInterrupted()) {
                // If the thread was intentionally interrupted (the player clicked back on the menu while hosting), don't show an error
                System.out.println("Host interrupted");
            } else {
                // Otherwise throw an error
                e.printStackTrace();
                throw new IOException();
            }
        }
    }

    public void Client() throws IOException {
        try {
            // Create a new network socket to connect to the host with
            clientSocket = new Socket(ip, port);
            // Make it reusable so the player doesn't have to reopen the game if they want to join a game again after playing
            clientSocket.setReuseAddress(true);

            System.out.println("Connected to the host.");

            // Update the connected status
            connected = true;

            // Set up input and output streams to be able to send/receive data between the two players
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            // Start a thread to constantly listen for incoming data
            new Thread(() -> listenForMessages(in)).start();

        } catch (IOException e) {
            // Make sure connected is false if something goes wrong
            connected = false;
            if (Thread.currentThread().isInterrupted()) {
                // If the thread was intentionally interrupted (the player clicked back on the menu while hosting), don't show an error
                System.out.println("Client interrupted");
            } else {
                // Otherwise throw an error
                e.printStackTrace();
                throw new IOException();
            }
        }
    }

    public void listenForMessages(DataInputStream in) {
        // Constantly checks for an incoming string and runs functions based on what the string is
        try {
            while (true) {
                // Assign the incoming data to a string
                String message = in.readUTF();
                System.out.println("P2: " + message);
                switch (message) {
                    // Run code depending on the string
                    case "start":
                        gp.gameState = gp.playState;
                        break;
                    case "left":
                        gp.npcs.get(2).direction = "left";
                        break;
                    case "!left":
                        gp.npcs.get(2).direction = "!left";
                        break;

                    case "right":
                        gp.npcs.get(2).direction = "right";
                        break;
                    case "!right":
                        gp.npcs.get(2).direction = "!right";
                        break;

                    case "up":
                        gp.npcs.get(2).direction = "up";
                        break;
                    case "!up":
                        gp.npcs.get(2).direction = "!up";
                        break;

                    case "down":
                        gp.npcs.get(2).direction = "down";
                        break;
                    case "!down":
                        gp.npcs.get(2).direction = "!down";
                        break;
                }
            }
        } catch (IOException e) {
            // If this function has an error, it's very likely because the players have disconnected
            // Set the connected status to false and return to the main menu if this happens
            System.out.println("Disconnected.");
            connected = false;
        }
    }

    public void sendMessage(DataOutputStream out, String message) {
        // Send a string to the other player using the dataOutputStream
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
