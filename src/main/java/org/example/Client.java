package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private  static  final String EXITCHAT = "/exit";
    private static Socket clientSocket = null;
    private static BufferedReader inMess;
    private static PrintWriter outMess;
    private static Scanner scannerConsole;


    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/settings.txt"));
        String[] line = reader.readLine().split(" = ");
        String host = line[1];
        line = reader.readLine().split(" = ");
        int port = Integer.parseInt(line[1]);
        reader.close();
        //System.out.println("host = " + host + " " + " port = " + port);

        clientSocket = new Socket(host, port);
        outMess = new PrintWriter(clientSocket.getOutputStream(), true);
        inMess = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        scannerConsole = new Scanner(System.in);

        AtomicBoolean flag = new AtomicBoolean(true);


        new Thread(() -> {
            try {
                while (true) {
                    if (!flag.get()) {
                        inMess.close();
                        clientSocket.close();
                        break;
                    }
                    if (inMess.ready()) {
                        String messFormServer = inMess.readLine();
                        System.out.println(messFormServer);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();



        new Thread(() -> {
            while (true) {
                if (scannerConsole.hasNext()) {
                    String mess = scannerConsole.nextLine();
                    if (mess.equalsIgnoreCase(EXITCHAT)) {
                        outMess.println(mess);
                        scannerConsole.close();
                        outMess.close();
                        flag.set(false);
                        break;
                    }
                    outMess.println(mess);
                }
            }
        }).start();
    }
}
