package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {

    private static Map<Integer, User> users = new HashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Start server");
        BufferedReader reader = new BufferedReader(new FileReader("src/main/settings.txt"));
        String[] line = reader.readLine().split(" = ");
        String host = line[1];
        line = reader.readLine().split(" = ");
        int port = Integer.parseInt(line[1]);
        reader.close();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    sendMessToAll("Порт данного клиента: " + clientSocket.getPort());
                    new Thread(() -> {
                        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                            User user = new User(clientSocket, out);
                            users.put(clientSocket.getPort(), user);
                            System.out.println(user);
                            waitMessAndSend(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void sendMessToAll(String mess) {
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            entry.getValue().sendMsg(mess);
            System.out.println("Сообщение отправлено");
        }
    }

    public static void waitMessAndSend(Socket clientSocket) {
        try (Scanner inMess = new Scanner(clientSocket.getInputStream())) {
            while (true) {
                if (inMess.hasNext()) {
                    String mess = inMess.nextLine();
                    switch (mess) {
                        default:
                            sendMessToAll(clientSocket.getPort() + ": " + mess);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
