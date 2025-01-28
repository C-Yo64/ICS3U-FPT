package main;

import entity.Player;
import entity.Player2;

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
            String x = "";
            String y = "";
            String direction = "";

            while (true) {
                // Assign the incoming data to a string
                String message = in.readUTF();
                System.out.println("P2: " + message);

                // This handles getting the position and direction of the other player
                // The x, y, and direction variable are sent in a string, so loop through that string and extract the data from it
                // This is done to optimize how many times I send data between the two players, because otherwise I would send 3 strings every 80ms instead of 1
                if (message.contains("PosX")) {
                    x = "";
                    y = "";
                    direction = "";

                    boolean countX = true;
                    boolean countY = false;
                    boolean countDir = false;
                    int yCounter = 0;
                    int directionCounter = 0;

                    for (int i = 0; i < message.length(); i++) {
                        // loop through until we get to the x position numbers, then add those numbers to a string that will be parsed later
                        if (countX) {
                            if (i >= 6 && message.charAt(i) != ',') {
                                x += message.charAt(i);
                            } else if (message.charAt(i) == ',') {
                                countX = false;
                            }
                        }
                        // once the character is Y, then I can do the same thing for the Y position value
                        if (message.charAt(i) == 'Y') {
                            countY = true;
                        }
                        if (countY) {
                            if (yCounter >= 3 && message.charAt(i) != ',') {
                                y += message.charAt(i);
                            } else if (message.charAt(i) == ',') {
                                countY = false;
                            }
                            yCounter++;
                        }

                        // once the character is D, then it's beginning to spell the word "direction", which will be followed by the string I want to get
                        if (message.charAt(i) == 'D') {
                            countDir = true;
                        }
                        if (countDir) {
                            if (directionCounter >= 11 && message.charAt(i) != ',') {
                                direction += message.charAt(i);
                            } else if (message.charAt(i) == ',') {
                                countDir = false;
                            }
                            directionCounter++;
                        }
                    }

                    System.out.println("P2 X: " + x);
                    System.out.println("P2 Y: " + y);
                    System.out.println("P2 Dir: " + direction);
                    try {
                        gp.npcs.get(2).worldX = Integer.parseInt(x);
                        gp.npcs.get(2).worldY = Integer.parseInt(y);
//                        gp.npcs.get(2).direction = direction;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                switch (message) {
                    // Run code depending on the string
                    case "start":
                        gp.gameState = gp.playState;
                        gp.player.initialize();
                        gp.playMusic(4);
                        break;
                    case "game over":
                        gp.menu.gameOver(false);
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


                    case "jump":
                        ((Player2) gp.npcs.get(2)).jumping = true;
                        break;
                    case "!jump":
                        ((Player2) gp.npcs.get(2)).jumping = false;
                        break;

                    case "hit":
                        // launch the player in the direction that player2 is facing
                        switch (direction) {
                            case "left":
                                gp.player.xMomentum -= gp.player.launchSpeed;
                                break;
                            case "right":
                                gp.player.xMomentum += gp.player.launchSpeed;
                                break;
                        }
                        // also launch upwards
                        gp.player.yMomentum = -gp.player.jumpSpeed;
                        gp.player.falling = true;
                        gp.playSE(2);
                        break;

                    case "attacking":
                        ((Player2) gp.npcs.get(2)).attacking = true;
                        break;
                    case "!attacking":
                        ((Player2) gp.npcs.get(2)).attacking = false;
                        break;
                }
            }
        } catch (IOException e) {
            // If this function has an error, it's very likely because the players have disconnected
            // Set the connected status to false and return to the main menu if this happens
            gp.menu.disconnected();
            System.out.println("Disconnected.");
            connected = false;
        }
    }

    public synchronized void sendMessage(DataOutputStream out, String message) {
        // Send a string to the other player using the dataOutputStream
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
